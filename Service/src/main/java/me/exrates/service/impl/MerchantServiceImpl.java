package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.exception.MerchantCurrencyBlockedException;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.*;
import static java.math.BigDecimal.valueOf;
import static me.exrates.model.enums.OperationType.*;

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
  private WalletService walletService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private InvoiceService invoiceService;

  @Autowired
  private BitcoinService bitcoinService;

  @Autowired
  private WithdrawRequestDao withdrawRequestDao;

  private static final Logger LOG = LogManager.getLogger("merchant");

  @Override
  public List<Merchant> findAllByCurrency(Currency currency) {
    return merchantDao.findAllByCurrency(currency.getId());
  }

  @Override
  public List<Merchant> findAll() {
    return merchantDao.findAll();
  }

  @Override
  public String resolveTransactionStatus(final Transaction transaction, final Locale locale) {
    if (transaction.getSourceType() == TransactionSourceType.INVOICE) {
      Integer statusId = invoiceService.getInvoiceRequestStatusByInvoiceId(transaction.getSourceId());
      InvoiceRequestStatusEnum invoiceRequestStatus = InvoiceRequestStatusEnum.convert(statusId);
      return messageSource.getMessage("merchants.invoice.".concat(invoiceRequestStatus.name()), null, locale);
    }
    if (transaction.getSourceType() == TransactionSourceType.WITHDRAW) {
      if (transaction.getOperationType() != OUTPUT) {
        return "";
      } else {
        WithdrawStatusEnum status = transaction.getWithdrawRequest().getStatus();
        return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
      }
    }
    if (transaction.getSourceType() == TransactionSourceType.BTC_INVOICE) {
      Integer statusId = bitcoinService.getPendingPaymentStatusByInvoiceId(transaction.getSourceId());
      PendingPaymentStatusEnum pendingPaymentStatus = PendingPaymentStatusEnum.convert(statusId);
      String message = messageSource.getMessage("merchants.invoice.".concat(pendingPaymentStatus.name()), null, locale);
      if (message.isEmpty()) {
        message = messageSource.getMessage("transaction.confirmations",
            new Object[]{
                transaction.getConfirmation(),
                BitcoinService.CONFIRMATION_NEEDED_COUNT
            }, locale);
      }
      return message;
    }
    if (transaction.isProvided()) {
      return messageSource.getMessage("transaction.provided", null, locale);
    }
    if (transaction.getConfirmation() == null || transaction.getConfirmation() == -1) {
      return messageSource.getMessage("transaction.notProvided", null, locale);
    }
    final String name = transaction.getCurrency().getName();
    final int acceptableConfirmations;
    switch (name) {
      case "EDRC":
        acceptableConfirmations = EDRCService.CONFIRMATIONS;
        break;
      case "BTC":
        acceptableConfirmations = BlockchainService.CONFIRMATIONS;
        break;
      default:
        throw new MerchantInternalException("Unknown confirmations number on " + transaction.getCurrency() +
            " " + transaction.getMerchant());
    }
    return messageSource.getMessage("transaction.confirmations",
        new Object[]{
            transaction.getConfirmation(),
            acceptableConfirmations
        }, locale);
  }

  @Override
  public String sendDepositNotification(final String toWallet,
                                        final String email,
                                        final Locale locale,
                                        final CreditsOperation creditsOperation,
                                        final String depositNotification) {
    final BigDecimal amount = creditsOperation
        .getAmount()
        .add(creditsOperation.getCommissionAmount());
    final String sumWithCurrency = BigDecimalProcessing.formatSpacePoint(amount, false) + " " +
        creditsOperation
            .getCurrency()
            .getName();
    final String notification = messageSource.getMessage(depositNotification,
        new Object[]{sumWithCurrency, toWallet},
        locale);
    final Email mail = new Email();
    mail.setTo(email);
    mail.setSubject(messageSource
        .getMessage("merchants.depositNotification.header", null, locale));
    mail.setMessage(notification);

    try {
      notificationService.createLocalizedNotification(email, NotificationEvent.IN_OUT,
          "merchants.depositNotification.header", depositNotification,
          new Object[]{sumWithCurrency, toWallet});
      sendMailService.sendInfoMail(mail);
    } catch (MailException e) {
      LOG.error(e);
    }
    return notification;
  }

  private Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies) {
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
  public Merchant findByNName(String name) {
    return merchantDao.findByName(name);
  }

  @Override
  public List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType) {
    if (currenciesId.isEmpty()) {
      return null;
    }
    return merchantDao.findAllByCurrencies(currenciesId, operationType);
  }

  @Override
  public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId) {
    return merchantDao.findAllMerchantCurrencies(currencyId, userService.getUserRoleFromSecurityContext());
  }

  @Override
  public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions() {
    return merchantDao.findMerchantCurrencyOptions();
  }

  @Override
  public Map<String, String> formatResponseMessage(CreditsOperation creditsOperation) {
    final OperationType operationType = creditsOperation.getOperationType();
    final String commissionPercent = creditsOperation
        .getCommission()
        .getValue()
        .setScale(2, ROUND_HALF_UP)
        .toString();
    String finalAmount = null;
    String sumCurrency = null;
    switch (operationType) {
      case INPUT:
        finalAmount = creditsOperation
            .getAmount()
            .setScale(2, ROUND_HALF_UP) + " "
            + creditsOperation
            .getCurrency()
            .getName();
        sumCurrency = creditsOperation
            .getAmount()
            .add(creditsOperation.getCommissionAmount())
            .setScale(2, ROUND_HALF_UP) + " "
            + creditsOperation
            .getCurrency()
            .getName();
        break;
      case OUTPUT:
        finalAmount = creditsOperation
            .getAmount()
            .subtract(creditsOperation.getCommissionAmount())
            .setScale(2, ROUND_HALF_UP) + " "
            + creditsOperation
            .getCurrency()
            .getName();
        sumCurrency = creditsOperation
            .getAmount()
            .setScale(2, ROUND_HALF_UP) + " "
            + creditsOperation
            .getCurrency()
            .getName();
        break;

    }
    final Map<String, String> result = new HashMap<>();
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

  @Override
  public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail) {
    checkMerchantBlock(payment.getMerchant(), payment.getCurrency(), payment.getOperationType());
    OperationType operationType = payment.getOperationType();
    BigDecimal amount = valueOf(payment.getSum());
    Merchant merchant = merchantDao.findById(payment.getMerchant());
    Currency currency = currencyService.findById(payment.getCurrency());
    String destination = payment.getDestination();
    MerchantImage merchantImage = new MerchantImage();
    merchantImage.setId(payment.getMerchantImage());
    try {
      if (!isPayable(merchant, currency, amount)) {
        LOG.warn("Merchant respond as not support this pay " + payment);
        return Optional.empty();
      }
    } catch (EmptyResultDataAccessException e) {
      final String exceptionMessage = "MerchantService".concat(operationType == INPUT ?
          "Input" : "Output");
      throw new UnsupportedMerchantException(exceptionMessage);
    }
    User user = userService.findByEmail(userEmail);
    Commission commissionByType = commissionService.findCommissionByTypeAndRole(operationType, user.getRole());
    BigDecimal commissionMerchant = commissionService.getCommissionMerchant(merchant.getName(), currency.getName(), operationType);
    BigDecimal commissionTotal = commissionByType.getValue().add(commissionMerchant)
        .setScale(currencyService.resolvePrecision(currency.getName()), ROUND_HALF_UP);
    BigDecimal commissionAmount =
        commissionTotal
            .multiply(amount)
            .divide(valueOf(100), currencyService.resolvePrecision(currency.getName()), ROUND_HALF_UP);
    commissionAmount = correctForMerchantFixedCommission(merchant.getName(), currency.getName(), operationType, commissionAmount);
    Wallet wallet = walletService.findByUserAndCurrency(user, currency);
    BigDecimal newAmount = payment.getOperationType() == INPUT ?
        amount :
        amount.subtract(commissionAmount).setScale(currencyService.resolvePrecision(currency.getName()), ROUND_DOWN);
    TransactionSourceType transactionSourceType = operationType == OUTPUT ? TransactionSourceType.WITHDRAW :
        TransactionSourceType.convert(merchant.getTransactionSourceTypeId());
    CreditsOperation creditsOperation = new CreditsOperation.Builder()
        .fullAmount(amount)
        .amount(newAmount)
        .commissionAmount(commissionAmount)
        .commission(commissionByType)
        .operationType(operationType)
        .user(user)
        .currency(currency)
        .wallet(wallet)
        .merchant(merchant)
        .destination(destination)
        .merchantImage(merchantImage)
        .transactionSourceType(transactionSourceType)
        .build();
    return Optional.of(creditsOperation);
  }

  private BigDecimal correctForMerchantFixedCommission(String merchantName, String currencyName, OperationType operationType, BigDecimal commissionAmount) {
    if (operationType != OUTPUT) {
      return commissionAmount;
    }
    BigDecimal merchantMinFixedCommission = commissionService.getMinFixedCommission(merchantName, currencyName);
    return commissionAmount.compareTo(merchantMinFixedCommission) < 0 ? merchantMinFixedCommission : commissionAmount;
  }

  private BigDecimal addMinimalCommission(BigDecimal commissionAmount, String name) {
    if (commissionAmount.compareTo(BigDecimal.ZERO) == 0) {
      if (currencyService.resolvePrecision(name) == 2) {
        commissionAmount = commissionAmount.add(new BigDecimal("0.01"));
      } else {
        commissionAmount = commissionAmount.add(new BigDecimal("0.00000001"));
      }
    }
    return commissionAmount;
  }

  private boolean isPayable(Merchant merchant, Currency currency, BigDecimal sum) {
    final BigDecimal minSum = merchantDao.getMinSum(merchant.getId(), currency.getId());
    return sum.compareTo(minSum) >= 0;
  }

  @Override
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(e -> e.getType())
        .collect(Collectors.toList());
    return withdrawRequestDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
  }

  @Override
  public boolean checkInputRequestsLimit(int merchantId, String email) {
    boolean inLimit = merchantDao.getInputRequests(merchantId, email) < 10;

    return inLimit;
  }

  @Override
  @Transactional
  public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    merchantDao.toggleMerchantBlock(merchantId, currencyId, operationType);
  }

  @Override
  @Transactional
  public void setBlockForAll(OperationType operationType, boolean blockStatus) {
    merchantDao.setBlockForAll(operationType, blockStatus);
  }

  @Override
  @Transactional
  public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {
    merchantDao.setBlockForMerchant(merchantId, currencyId, operationType, blockStatus);
  }

  @Override
  @Transactional
  public List<MerchantCurrencyLifetimeDto> getMerchantCurrencyWithRefillLifetime() {
    return merchantDao.findMerchantCurrencyWithRefillLifetime();
  }

  @Override
  @Transactional
  public MerchantCurrencyLifetimeDto getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(
      Integer merchantId,
      Integer currencyId) {
    return merchantDao.findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(merchantId, currencyId);
  }

  private void checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    boolean isBlocked = merchantDao.checkMerchantBlock(merchantId, currencyId, operationType);
    if (isBlocked) {
      throw new MerchantCurrencyBlockedException("Operation " + operationType + " is blocked for this currency! ");
    }
  }


}
