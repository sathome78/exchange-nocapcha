package me.exrates.service.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.PendingPaymentFlatDto;
import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import me.exrates.service.exception.invoice.InvoiceAcceptionException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.InvoiceUnexpectedHashException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.util.BitCoinUtils.satoshiToBtc;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class BitcoinServiceImpl implements BitcoinService {

  private final Logger LOG = LogManager.getLogger("merchant");

  private WalletAppKit kit;
  private final ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

  private final BitcoinWalletAppKit kitBefore;
  private final PendingPaymentDao paymentDao;
  private final TransactionService transactionService;
  private final AlgorithmService algorithmService;
  private final BTCTransactionDao btcTransactionDao;
  private final UserService userService;
  private final NotificationService notificationService;
  private final MerchantService merchantService;
  private final CurrencyService currencyService;
  private final WalletDao walletDao;
  private final CompanyWalletService companyWalletService;


  private static final BigDecimal SATOSHI = new BigDecimal(100_000_000L);
  private static final int decimalPlaces = 8;

  private final Function<String, Supplier<IllegalStateException>> throwIllegalStateEx = (address) ->
      () -> new IllegalStateException("Pending payment with address " + address + " is not exist");

  @Autowired
  public BitcoinServiceImpl(final BitcoinWalletAppKit kitBefore,
                            final PendingPaymentDao paymentDao,
                            final TransactionService transactionService,
                            final AlgorithmService algorithmService,
                            final BTCTransactionDao btcTransactionDao,
                            final UserService userService,
                            final NotificationService notificationService,
                            final MerchantService merchantService,
                            final CurrencyService currencyService,
                            final WalletDao walletDao,
                            final CompanyWalletService companyWalletService) {
    this.kitBefore = kitBefore;
    this.paymentDao = paymentDao;
    this.transactionService = transactionService;
    this.algorithmService = algorithmService;
    this.btcTransactionDao = btcTransactionDao;
    this.userService = userService;
    this.notificationService = notificationService;
    this.merchantService = merchantService;
    this.currencyService = currencyService;
    this.walletDao = walletDao;
    this.companyWalletService = companyWalletService;
  }

  private String extractRecipientAddress(final List<TransactionOutput> outputs) {
    if (outputs.size() < 1) {
      throw new IllegalArgumentException("List with transaction outputs is empty");
    }
    return outputs.stream()
        .filter(tx -> tx.isMine(kit.wallet()))
        .map(tx -> tx.getScriptPubKey().getToAddress(kit.params(), true).toBase58())
        .findFirst()
        .orElseThrow(IllegalStateException::new); //it will never happen
  }

  @PostConstruct
  void startBitcoin() {

    Currency currency = currencyService.findByName("BTC");
    Merchant merchant = merchantService.findAllByCurrency(currency).get(0);

    merchantService.setBlockForMerchant(merchant.getId(), currency.getId(), OperationType.INPUT, true);

    new Thread(new Runnable() {
      @Override
      public void run() {
        kitBefore.startupWallet();
        kit = kitBefore.kit();
      }
    }).start();

    Timer myTimer = new Timer();
    myTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (kit != null) {
          init();
          myTimer.cancel();
          myTimer.purge();
          merchantService.setBlockForMerchant(merchant.getId(), currency.getId(), OperationType.INPUT, false);
          LOG.debug("BTC init completed");
        }
      }
    }, 0L, 10000);
//    }, 0L, 60L * 5000);
  }

  @Transactional
  private void changeTransactionConfidenceForPendingPayment(
      Integer invoiceId,
      int confidenceLevel) {
    transactionService.updateTransactionConfirmation(invoiceId, confidenceLevel);
  }

  public void init() {
    try {
      InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
      kit.wallet().addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
        final String address = extractRecipientAddress(tx.getOutputs());
        System.out.println(address);
        if (paymentDao.existsPendingPaymentWithAddressAndStatus(address, Arrays.asList(beginStatus.getCode()))) {
          String txHash = tx.getHashAsString();
          PendingPaymentStatusDto pendingPayment = paymentDao.setStatusAndHashByAddressAndStatus(
              address,
              beginStatus.getCode(),
              beginStatus.nextState(BCH_EXAMINE).getCode(),
              txHash)
              .orElseThrow(() -> new InvoiceNotFoundException(address));
          final Integer invoiceId = pendingPayment.getInvoiceId();
          List<ListenableFuture<TransactionConfidence>> confirmations = IntStream.rangeClosed(1, CONFIRMATION_NEEDED_COUNT)
              .mapToObj(x -> tx.getConfidence().getDepthFuture(x))
              .collect(toList());
          confirmations.forEach(confidence -> confidence.addListener(() -> {
            try {
              Integer confirmsCount = confidence.get().getDepthInBlocks();
              changeTransactionConfidenceForPendingPayment(invoiceId, confirmsCount);
              if (confirmsCount >= CONFIRMATION_NEEDED_COUNT) {
                BigDecimal factPaymentAmount = satoshiToBtc(tx.getValue(wallet).getValue());
                provideTransaction(invoiceId, txHash, factPaymentAmount, null);
              }
            } catch (final ExecutionException | InterruptedException e) {
              LOG.error(e);
            } catch (Exception e) {
              LOG.error(ExceptionUtils.getStackTrace(e));
            }
          }, pool));
        }
      });
    } catch (Exception e) {
      LOG.error(ExceptionUtils.getStackTrace(e));
    }
  }

  @PreDestroy
  public void preDestroy() {
    try {
      pool.awaitTermination(25, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOG.error(e);
    }
  }

  private Address address() {
    boolean isFreshAddress = false;
    Address address = kit.wallet().freshReceiveAddress();

    final List<Integer> unclosedPendingPaymentStatesList = PendingPaymentStatusEnum.getMiddleStatesSet().stream()
        .map(InvoiceStatus::getCode)
        .collect(toList());

    if (paymentDao.existsPendingPaymentWithAddressAndStatus(address.toString(), unclosedPendingPaymentStatesList)) {
      final int LIMIT = 2000;
      int i = 0;
      while (!isFreshAddress && i++ < LIMIT) {
        address = kit.wallet().freshReceiveAddress();
        isFreshAddress = !paymentDao.existsPendingPaymentWithAddressAndStatus(address.toString(), unclosedPendingPaymentStatesList);
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
    Address address = address();
    PendingPayment payment = new PendingPayment();
    payment.setInvoiceId(transaction.getId());
    payment.setAddress(address.toBase58());
    payment.setTransactionHash(computeTransactionHash(transaction, address));
    payment.setPendingPaymentStatus(PendingPaymentStatusEnum.getBeginState());
    /**/
    paymentDao.create(payment);
    /*id (invoice_id) in pendingPayment is the id of the corresponding transaction. So source_id equals invoice_id*/
    transactionService.setSourceId(transaction.getId(), transaction.getId());
    return payment;
  }

  private String computeTransactionHash(final me.exrates.model.Transaction tx, Address address) {
    if (isNull(tx) || isNull(tx.getCommission()) || isNull(tx.getCommissionAmount())) {
      throw new IllegalArgumentException("Argument itself or contains null");
    }
    final String target = new StringJoiner(":")
        .add(String.valueOf(tx.getId()))
        .add(address.toBase58())
        .toString();
    return algorithmService.sha256(target);
  }

  @Override
  @Transactional
  public void provideTransaction(Integer pendingPaymentId, String hash, BigDecimal factAmount, String acceptanceUserEmail) throws Exception {
    if (factAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalInvoiceAmountException(factAmount.toString());
    }
    PendingPayment pendingPayment = paymentDao.findByIdAndBlock(pendingPaymentId)
        .orElseThrow(() -> new InvoiceNotFoundException(pendingPaymentId.toString()));
    InvoiceActionTypeEnum action = acceptanceUserEmail == null ? ACCEPT_AUTO : ACCEPT_MANUAL;
    if (action == ACCEPT_AUTO && !hash.equals(pendingPayment.getHash())) {
      throw new InvoiceUnexpectedHashException(String.format("hash stored in invoice: %s actual get from BCH: %s", pendingPayment.getHash(), hash));
    }
    InvoiceStatus newStatus = pendingPayment.getPendingPaymentStatus().nextState(action);
    pendingPayment.setPendingPaymentStatus(newStatus);
    Transaction transaction = pendingPayment.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + pendingPaymentId);
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transaction.getId());
    }
    BigDecimal amountByInvoice = BigDecimalProcessing.doAction(transaction.getAmount(), transaction.getCommissionAmount(), ActionType.ADD);
    if (amountByInvoice.compareTo(factAmount) != 0) {
      transaction.setAmount(factAmount);
      transactionService.updateTransactionAmount(transaction);
    }
    WalletOperationData walletOperationData = new WalletOperationData();
    walletOperationData.setOperationType(transaction.getOperationType());
    walletOperationData.setWalletId(transaction.getUserWallet().getId());
    walletOperationData.setAmount(transaction.getAmount());
    walletOperationData.setBalanceType(ACTIVE);
    walletOperationData.setCommission(transaction.getCommission());
    walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
    walletOperationData.setSourceType(transaction.getSourceType());
    walletOperationData.setSourceId(pendingPaymentId);
    walletOperationData.setTransaction(transaction);
    WalletTransferStatus walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
      String message = "error while accepting pendingPayment id (invoice_id) = " + pendingPaymentId;
      LOG.error("\n\t" + message);
      throw new InvoiceAcceptionException(message);
    }
    /**/
    companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
        transaction.getCommissionAmount());
    /**/
    pendingPayment.setAcceptanceUserEmail(acceptanceUserEmail);
    pendingPayment.setPendingPaymentStatus(newStatus);
    pendingPayment.setHash(hash);
    paymentDao.updateAcceptanceStatus(pendingPayment);
    /**/
    notificationService.notifyUser(pendingPayment.getUserId(), NotificationEvent.IN_OUT, "paymentRequest.accepted.title",
        "paymentRequest.accepted.message", new Integer[]{pendingPaymentId});
  }

  @Override
  @Transactional(readOnly = true)
  public List<PendingPaymentFlatDto> getBitcoinTransactions() {
    List<Integer> pendingPaymentStatusIdList = PendingPaymentStatusEnum.getAvailableForActionStatusesList(ACCEPT_MANUAL).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    return paymentDao.findFlattenDtoByStatus(pendingPaymentStatusIdList);
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getPendingPaymentStatusByInvoiceId(Integer invoiceId) {
    return paymentDao.getStatusById(invoiceId);
  }
}
