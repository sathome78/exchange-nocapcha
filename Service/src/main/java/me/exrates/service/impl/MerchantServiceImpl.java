package me.exrates.service.impl;

import java.util.Locale;
import javafx.util.Pair;
import me.exrates.dao.MerchantDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import me.exrates.service.exception.UnsupportedMerchantException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class MerchantServiceImpl implements MerchantService{

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
    private ApplicationContext context;

    private static final Logger logger = LogManager.getLogger("merchant");

    @Override
    public Merchant create(Merchant merchant) {
        return merchantDao.create(merchant);
    }

    @Override
    public List<Merchant> findAllByCurrency(Currency currency) {
        return merchantDao.findAllByCurrency(currency.getId());
    }

    @Override
    public String sendDepositNotification(final String toWallet,
        final String email, final Locale locale,
        final CreditsOperation creditsOperation)
    {
        final BigDecimal amount = creditsOperation
            .getAmount()
            .add(creditsOperation.getCommissionAmount());
        final String sumWithCurrency = amount.stripTrailingZeros() +
            creditsOperation
                .getCurrency()
                .getName();
        final String notification = String
            .format(context.getMessage("merchants.depositNotification",null,locale),
                sumWithCurrency, toWallet);
        final Email mail = new Email();
        mail.setTo(email);
        mail.setSubject("Exrates Payment Invoice");
        mail.setMessage(sumWithCurrency);
        try {
            sendMailService.sendMail(mail);
            logger.info("Sended email :"+email);
        } catch (MailException e) {
            logger.error(e);
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
                logger.warn("Merchant respond as not support this pay"+payment);
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            final String exceptionMessage = "MerchantService".concat(operationType == OperationType.INPUT ?
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

        final CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .amount(amount)
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
