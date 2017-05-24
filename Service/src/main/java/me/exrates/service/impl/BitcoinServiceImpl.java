package me.exrates.service.impl;

import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.PendingPaymentStatusEnum.EXPIRED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@PropertySource(value = {"classpath:/job.properties"})
public class BitcoinServiceImpl implements BitcoinService {

  private final Logger LOG = LogManager.getLogger("merchant");

  @Value("${btcInvoice.blockNotifyUsers}")
  private Boolean BLOCK_NOTIFYING;
  


  @Autowired
  private PendingPaymentDao paymentDao;
  @Autowired
  private TransactionService transactionService;
  @Autowired
  private AlgorithmService algorithmService;
  @Autowired
  private NotificationService notificationService;
  
  @Autowired
  private MerchantService merchantService;
  
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private CoreWalletService bitcoinWalletService;
  @Autowired
  private BitcoinTransactionService bitcoinTransactionService;
  
  private String walletPassword;
  
  private String backupFolder;
  
  private String nodePropertySource;
  
  
  public BitcoinServiceImpl(String propertySource) {
    Properties props = new Properties();
    try {
      props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
      this.walletPassword = props.getProperty("wallet.password");
      this.backupFolder = props.getProperty("backup.folder");
      this.nodePropertySource = props.getProperty("node.propertySource");
    } catch (IOException e) {
      LOG.error(e);
    }
    
  }
  
  @PostConstruct
  void startBitcoin() {
    bitcoinWalletService.initCore(nodePropertySource);
  }
   

  private String address() {
    boolean isFreshAddress = false;
    String address = bitcoinWalletService.getNewAddress(walletPassword);

    final List<Integer> unclosedPendingPaymentStatesList = PendingPaymentStatusEnum.getMiddleStatesSet().stream()
        .map(InvoiceStatus::getCode)
        .collect(toList());

    if (paymentDao.existsPendingPaymentWithAddressAndStatus(address, unclosedPendingPaymentStatesList)) {
      final int LIMIT = 2000;
      int i = 0;
      while (!isFreshAddress && i++ < LIMIT) {
        address = bitcoinWalletService.getNewAddress(walletPassword);
        isFreshAddress = !paymentDao.existsPendingPaymentWithAddressAndStatus(address, unclosedPendingPaymentStatesList);
      }
      if (i >= LIMIT) {
        throw new IllegalStateException("Can`t generate fresh address");
      }
    }

    return address;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public PendingPayment createInvoice(CreditsOperation operation) {
    Transaction transaction = transactionService.createTransactionRequest(operation);
    String address = address();
    PendingPayment payment = new PendingPayment();
    payment.setInvoiceId(transaction.getId());
    payment.setAddress(address);
    payment.setTransactionHash(computeTransactionHash(transaction, address));
    payment.setPendingPaymentStatus(PendingPaymentStatusEnum.getBeginState());
    /**/
    paymentDao.create(payment);
    /*id (invoice_id) in pendingPayment is the id of the corresponding transaction. So source_id equals invoice_id*/
    transactionService.setSourceId(transaction.getId(), transaction.getId());
    return payment;
  }

  private String computeTransactionHash(final me.exrates.model.Transaction tx, String address) {
    if (isNull(tx) || isNull(tx.getCommission()) || isNull(tx.getCommissionAmount())) {
      throw new IllegalArgumentException("Argument itself or contains null");
    }
    final String target = new StringJoiner(":")
        .add(String.valueOf(tx.getId()))
        .add(address)
        .toString();
    return algorithmService.sha256(target);
  }
  
  @Override
  @Transactional
  public void provideTransaction(Integer pendingPaymentId, String hash, BigDecimal factAmount, String acceptanceUserEmail) throws Exception {
    bitcoinTransactionService.provideBtcTransaction(pendingPaymentId, hash, factAmount, acceptanceUserEmail);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PendingPaymentFlatDto> getBitcoinTransactions() {
    List<Integer> pendingPaymentStatusIdList = PendingPaymentStatusEnum.getAvailableForActionStatusesList(ACCEPT_MANUAL).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    return paymentDao.findFlattenDtoByStatus(
        TransactionSourceType.BTC_INVOICE.name(),
        pendingPaymentStatusIdList);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PendingPaymentFlatDto> getBitcoinTransactionsForCurrencyPermitted(Integer requesterUserId) {
    return getBitcoinTransactionsByStatusesForCurrencyPermitted(requesterUserId,
            PendingPaymentStatusEnum.getAvailableForActionStatusesList(ACCEPT_MANUAL));
  }
  
  @Override
  @Transactional(readOnly = true)
  public List<PendingPaymentFlatDto> getBitcoinTransactionsAcceptedForCurrencyPermitted(Integer requesterUserId) {
    return getBitcoinTransactionsByStatusesForCurrencyPermitted(requesterUserId, PendingPaymentStatusEnum.getAcceptedStatesSet());
  }
  
  private List<PendingPaymentFlatDto> getBitcoinTransactionsByStatusesForCurrencyPermitted(Integer requesterUserId,
                                                                                 Collection<InvoiceStatus> states) {
    List<Integer> pendingPaymentStatusIdList = states.stream().map(InvoiceStatus::getCode).collect(toList());
    return paymentDao.findFlattenDtoByStatusAndCurrencyPermittedForUser(
            TransactionSourceType.BTC_INVOICE.name(), pendingPaymentStatusIdList,
            requesterUserId);
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getPendingPaymentStatusByInvoiceId(Integer invoiceId) {
    return paymentDao.getStatusById(invoiceId);
  }

  @Override
  @Transactional
  public Integer clearExpiredInvoices(Integer intervalMinutes) throws Exception {
    List<Integer> pendingPaymentStatusIdList = PendingPaymentStatusEnum.getAvailableForActionStatusesList(EXPIRE).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    Optional<LocalDateTime> nowDate = paymentDao.getAndBlockBySourceTypeAndIntervalAndStatus(
        TransactionSourceType.BTC_INVOICE.name(),
        intervalMinutes,
        pendingPaymentStatusIdList);
    if (nowDate.isPresent()) {
      paymentDao.setNewStatusBySourceTypeAndDateIntervalAndStatus(
          TransactionSourceType.BTC_INVOICE.name(),
          nowDate.get(),
          intervalMinutes,
          EXPIRED.getCode(),
          pendingPaymentStatusIdList);
      List<InvoiceUserDto> userForNotificationList = paymentDao.findInvoicesListBySourceTypeAndStatusChangedAtDate(
          TransactionSourceType.BTC_INVOICE.name(),
          EXPIRED.getCode(),
          nowDate.get());
      if (!BLOCK_NOTIFYING) {
        for (InvoiceUserDto invoice : userForNotificationList) {
          notificationService.notifyUser(invoice.getUserId(), NotificationEvent.IN_OUT, "merchants.invoice.expired.title",
              "merchants.btc_invoice.expired.message", new Integer[]{invoice.getInvoiceId()});
        }
      }
      return userForNotificationList.size();
    } else {
      return 0;
    }
  }


  @Override
  @Transactional
  public void revoke(Integer pendingPaymentId) throws Exception {
    PendingPayment pendingPayment = paymentDao.findByIdAndBlock(pendingPaymentId)
        .orElseThrow(() -> new InvoiceNotFoundException(pendingPaymentId.toString()));
    InvoiceActionTypeEnum action = REVOKE;
    InvoiceStatus newStatus = pendingPayment.getPendingPaymentStatus().nextState(action);
    pendingPayment.setPendingPaymentStatus(newStatus);
    Transaction transaction = pendingPayment.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + pendingPaymentId);
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transaction.getId());
    }
    pendingPayment.setPendingPaymentStatus(newStatus);
    paymentDao.updateAcceptanceStatus(pendingPayment);
  }

  @Override
  @Transactional(readOnly = true)
  public PendingPaymentSimpleDto getPendingPaymentSimple(Integer pendingPaymentId) throws Exception {
    return paymentDao.findById(pendingPaymentId)
        .orElseThrow(() -> new InvoiceNotFoundException(pendingPaymentId.toString()));
  }
  
  
  @Override
  public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    BigDecimal withdrawAmount = new BigDecimal(withdrawMerchantOperationDto.getAmount());
    bitcoinWalletService.sendToAddressAuto(withdrawMerchantOperationDto.getAccountTo(), withdrawAmount, walletPassword);
  }
  
  @Override
  @Transactional
  public Map<String, String> prepareBitcoinPayment(Payment payment, String email, String currencyNameForQr, Locale locale) {
    CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);
      PendingPayment pendingPayment = createInvoice(creditsOperation);
      String notification = merchantService
              .sendDepositNotification(Optional.ofNullable(pendingPayment
                              .getAddress()).orElseThrow(
                      () -> new MerchantInternalException("Address not presented"))
                      , email, locale, creditsOperation, "merchants.depositNotification.body");
      Map<String, String> responseMap = new TreeMap<>();
      responseMap.put("notification", notification);
      responseMap.put("qr", currencyNameForQr + ":" + Optional.ofNullable(pendingPayment
              .getAddress()).orElseThrow(
              () -> new MerchantInternalException("Address not presented")) + "?amount="
              + creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()).doubleValue() + "&message=Donation%20for%20project%20Exrates");
    
      return responseMap;
  }
  
  @Override
  @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  public void backupWallet() {
    bitcoinWalletService.backupWallet(backupFolder);
  }
  
  @Override
  public BtcWalletInfoDto getWalletInfo() {
    return bitcoinWalletService.getWalletInfo();
  }
  
  @Override
  public List<BtcTransactionHistoryDto> listAllTransactions() {
    return bitcoinWalletService.listAllTransactions();
  }
  
  @Override
  public BigDecimal estimateFee(int blockCount) {
    return bitcoinWalletService.estimateFee(blockCount);
  }
  
  @Override
  public BigDecimal getActualFee() {
    return bitcoinWalletService.getActualFee();
  }
  
  @Override
  public void setTxFee(BigDecimal fee) {
    bitcoinWalletService.setTxFee(fee);
  }
  
  @Override
  public void submitWalletPassword(String password) {
    bitcoinWalletService.submitWalletPassword(password);
  }
  
  @Override
  public String sendToMany(Map<String, BigDecimal> payments) {
    return bitcoinWalletService.sendToMany(payments);
  }
}
