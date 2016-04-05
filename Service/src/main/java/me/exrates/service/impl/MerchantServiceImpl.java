package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.Commission;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.UnsupportedMerchantException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.WithdrawalRequestStatus.ACCEPTED;
import static me.exrates.model.enums.WithdrawalRequestStatus.DECLINED;
import static me.exrates.model.enums.WithdrawalRequestStatus.NEW;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WithdrawRequestDao withdrawRequestDao;

    @Autowired
    private WalletService walletService;

    private static final Logger LOGGER = LogManager.getLogger("merchant");
    private static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.CEILING);

    @Override
    public Map<String, String> acceptWithdrawalRequest(final int requestId,
                                                       final Locale locale,
                                                       final Principal principal) {
        final Optional<WithdrawRequest> withdraw = withdrawRequestDao.findById(requestId);
        if (!withdraw.isPresent()) {
            return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError",null,locale));
        }
        final WithdrawRequest request = withdraw.get();
        request.setProcessedBy(principal.getName());
        withdrawRequestDao.update(request);
        transactionService.provideTransaction(request.getTransaction());
        sendWithdrawalNotification(request, ACCEPTED, locale);
        final HashMap<String, String> params = new HashMap<>();
        final String message = messageSource.getMessage("merchants.WithdrawRequestAccept", null, locale);
        params.put("success", message);
        params.put("email", principal.getName());
        return params;
    }

    @Override
    public Map<String, String> declineWithdrawalRequest(final int requestId, final Locale locale) {
        final Optional<WithdrawRequest> withdraw = withdrawRequestDao.findById(requestId);
        if (!withdraw.isPresent()) {
            return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError",null,locale));
        }
        final WithdrawRequest request = withdraw.get();
        final Transaction transaction = request.getTransaction();
        withdrawRequestDao.delete(request);
        transactionService.invalidateTransaction(request.getTransaction());
        final BigDecimal amount = transaction.getAmount().add(transaction.getCommissionAmount());
        walletService.withdrawReservedBalance(transaction.getUserWallet(),amount);
        walletService.depositActiveBalance(transaction.getUserWallet(),amount);
        sendWithdrawalNotification(request, DECLINED, locale);
        return singletonMap("success",messageSource.getMessage("merchants.WithdrawRequestDecline",null,locale));
    }

    @Override
    public List<WithdrawRequest> findAllWithdrawRequests() {
        return withdrawRequestDao.findAll();
    }

    @Override
    public List<Merchant> findAllByCurrency(Currency currency) {
        return merchantDao.findAllByCurrency(currency.getId());
    }

    @Override
    @Transactional
    public Map<String, String> withdrawRequest(final CreditsOperation creditsOperation,
                                               final Locale locale, final Principal principal) {
        final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        final BigDecimal reserved = transaction
                .getAmount()
                .add(transaction.getCommissionAmount(), MATH_CONTEXT);
        walletService.depositReservedBalance(transaction.getUserWallet(), reserved);
        final WithdrawRequest request = new WithdrawRequest();
        request.setUserEmail(principal.getName());
        creditsOperation
                .getDestination()
                .ifPresent(request::setWallet);
        request.setTransaction(transaction);
        withdrawRequestDao.create(request);
        String notification=null;
        try {
            notification = sendWithdrawalNotification(request, NEW, locale);
        } catch (final MailException e) {
            LOGGER.error(e);
        }
        return singletonMap("success", notification);
    }

    @Override
    public String sendWithdrawalNotification(final WithdrawRequest withdrawRequest,
                                             final WithdrawalRequestStatus status ,final Locale locale) {
        final String notification;
        final Transaction transaction = withdrawRequest.getTransaction();
        final Object[] messageParams = {
                transaction
                        .getId(),
                transaction
                        .getMerchant()
                        .getDescription()
        };
        switch (status) {
            case NEW : notification = messageSource
                    .getMessage("merchants.withdrawNotification", messageParams, locale);
                break;
            case ACCEPTED: notification = messageSource
                    .getMessage("merchants.withdrawNotificationAccepted", messageParams, locale);
                break;
            case DECLINED: notification = messageSource
                    .getMessage("merchants.withdrawNotificationDeclined", messageParams, locale);
                break;
            default:
                throw new MerchantInternalException(status + "Withdrawal status is invalid");
        }
        final Email email = new Email();
        email.setMessage(notification);
        email.setSubject(messageSource
                .getMessage("merchants.withdrawNotification.header", null, locale));
        email.setTo(withdrawRequest.getUserEmail());
        try {
            sendMailService.sendMail(email);
            LOGGER.info("Sanded email :"+email);
        } catch (MailException e) {
            LOGGER.error(e);
        }
        return notification;
    }


    @Override
    public String sendDepositNotification(final String toWallet,
        final String email, final Locale locale,
        final CreditsOperation creditsOperation)
    {
        return sendDepositNotification
            (toWallet, email, locale, creditsOperation, null);
    }

    @Override
    public String sendDepositNotification(final String toWallet,
        final String email, final Locale locale,
        final CreditsOperation creditsOperation, final BigDecimal externalFee)
    {
        final BigDecimal amount = creditsOperation
            .getAmount()
            .add(creditsOperation.getCommissionAmount())
            .add(Objects.nonNull(externalFee) ? externalFee : BigDecimal.ZERO);
        final String sumWithCurrency = amount.stripTrailingZeros() +
            creditsOperation
                .getCurrency()
                .getName();
        final String notification = String
            .format(messageSource
                .getMessage(Objects.isNull(externalFee) ?
                    "merchants.depositNotification.body" :
                    "merchants.depositNotificationWithFee.body",null,locale),
                sumWithCurrency, toWallet);
        final Email mail = new Email();
        mail.setTo(email);
        mail.setSubject(messageSource
            .getMessage("merchants.depositNotification.header",null,locale));
        mail.setMessage(sumWithCurrency);
        try {
            sendMailService.sendMail(mail);
            LOGGER.info("Sanded email :"+email);
        } catch (MailException e) {
            LOGGER.error(e);
        }
        return notification;
    }

    @Override
    public Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies) {
        return currencies.stream()
                .map(Currency::getId)
                .map(currencyId -> new Pair<>(currencyId, merchantDao.findAllByCurrency(currencyId)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public Merchant findById(int id) {
        return merchantDao.findById(id);
    }

    @Override
    public List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId) {
        if (currenciesId.isEmpty()) {
            return null;
        }
        return merchantDao.findAllByCurrencies(currenciesId);
    }

    @Override
    public Map<String, String> formatResponseMessage(CreditsOperation creditsOperation) {
        final OperationType operationType = creditsOperation.getOperationType();
        final String commissionPercent = creditsOperation
                .getCommission()
                .getValue()
                .setScale(2,BigDecimal.ROUND_CEILING)
                .toString();
        String finalAmount=null;
        String sumCurrency=null;
        switch (operationType) {
            case INPUT:
                finalAmount = creditsOperation
                        .getAmount()
                        .setScale(2,BigDecimal.ROUND_CEILING) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                sumCurrency = creditsOperation
                        .getAmount()
                        .add(creditsOperation.getCommissionAmount())
                        .setScale(2,BigDecimal.ROUND_CEILING) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                break;
            case OUTPUT:
                finalAmount = creditsOperation
                        .getAmount()
                        .subtract(creditsOperation.getCommissionAmount())
                        .setScale(2,BigDecimal.ROUND_CEILING) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                sumCurrency = creditsOperation
                        .getAmount()
                        .setScale(2,BigDecimal.ROUND_CEILING) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                break;

        }
        final Map<String,String> result = new HashMap<>();
        result.put("commissionPercent", commissionPercent);
        result.put("sumCurrency", sumCurrency);
        result.put("finalAmount", finalAmount);
        return result;
    }

    @Override
    public Map<String, String> formatResponseMessage(Transaction transaction) {
        final CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .operationType(transaction.getOperationType())
                .amount(transaction.getAmount())
                .commissionAmount(transaction.getCommissionAmount())
                .commission(transaction.getCommission())
                .currency(transaction.getCurrency())
                .build();
        return formatResponseMessage(creditsOperation);

    }

    public Optional<CreditsOperation> prepareCreditsOperation(Payment payment,String userEmail) {
        final OperationType operationType = payment.getOperationType();
        final BigDecimal amount = BigDecimal.valueOf(payment.getSum());
        final Merchant merchant = merchantDao.findById(payment.getMerchant());
        final Currency currency = currencyService.findById(payment.getCurrency());
        final String destination = payment.getDestination();
        try {
            if (!isPayable(merchant,currency,amount)) {
                LOGGER.warn("Merchant respond as not support this pay " + payment);
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            final String exceptionMessage = "MerchantService".concat(operationType == INPUT ?
                    "Input" : "Output");
            throw new UnsupportedMerchantException(exceptionMessage);
        }
        final Commission commissionByType = commissionService.findCommissionByType(operationType);
        final BigDecimal commissionAmount = 
                 commissionByType.getValue()
                .setScale(9,BigDecimal.ROUND_CEILING)
                .multiply(amount)
                .divide(BigDecimal.valueOf(100),BigDecimal.ROUND_CEILING);
        final User user = userService.findByEmail(userEmail);
        final BigDecimal newAmount = payment.getOperationType() == INPUT ?
                amount :
                amount.subtract(commissionAmount, MATH_CONTEXT);
        final CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .amount(newAmount)
                .commissionAmount(commissionAmount)
                .commission(commissionByType)
                .operationType(operationType)
                .user(user)
                .currency(currency)
                .merchant(merchant)
                .destination(destination)
                .build();
        return Optional.of(creditsOperation);
    }

    private boolean isPayable(Merchant merchant, Currency currency, BigDecimal sum) {
        final BigDecimal minSum = merchantDao.getMinSum(merchant.getId(), currency.getId());
        return sum.compareTo(minSum) >= 0;
    }
}
