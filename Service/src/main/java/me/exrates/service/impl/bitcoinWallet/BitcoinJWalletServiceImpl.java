package me.exrates.service.impl.bitcoinWallet;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.BitcoinWalletAppKit;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.BitcoinTransactionService;
import me.exrates.service.BitcoinWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.NotImplimentedMethod;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by OLEG on 16.03.2017.
 */
@Log4j2
public class BitcoinJWalletServiceImpl implements BitcoinWalletService {
  
  private WalletAppKit kit;
  
  
  @Autowired
  private BitcoinWalletAppKit kitBefore;
  
  @Autowired
  private CurrencyService currencyService;
  
  @Autowired
  private MerchantService merchantService;
  
  @Autowired
  private BitcoinTransactionService bitcoinTransactionService;
  
  private final ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
  
  
  @Override
  public void initBitcoin() {
    Currency currency = currencyService.findByName("BTC");
    Merchant merchant = merchantService.findAllByCurrency(currency).get(0);
  
    merchantService.setBlockForMerchant(merchant.getId(), currency.getId(), OperationType.INPUT, true);
  
    new Thread(() -> {
      kitBefore.startupWallet();
      kit = kitBefore.kit();
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
          log.debug("BTC init completed");
        }
      }
    }, 0L, 10000);
//    }, 0L, 60L * 5000);
    
  }
  
  public void init() {
    /*try {

      TODO REFILL
      InvoiceStatus beginStatus = RefillStatusEnum.getBeginState();
      kit.wallet().addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
        final String address = extractRecipientAddress(tx.getOutputs());
        System.out.println(address);
        if (bitcoinTransactionService.existsPendingPaymentWithStatusAndAddress(beginStatus, address)) {
          try {
            String txHash = tx.getHashAsString();
            BigDecimal factAmount = satoshiToBtc(tx.getValue(wallet).getValue());
            PendingPaymentStatusDto pendingPayment = bitcoinTransactionService.markStartConfirmationProcessing(address, txHash, factAmount);
            final Integer invoiceId = pendingPayment.getInvoiceId();
            List<ListenableFuture<TransactionConfidence>> confirmations = IntStream.rangeClosed(1, CONFIRMATION_NEEDED_COUNT)
                    .mapToObj(x -> tx.getConfidence().getDepthFuture(x))
                    .collect(toList());
            confirmations.forEach(confidence -> confidence.addListener(() -> {
              try {
                Integer confirmsCount = confidence.get().getDepthInBlocks();
                bitcoinTransactionService.changeTransactionConfidenceForPendingPayment(invoiceId, confirmsCount);
                if (confirmsCount >= CONFIRMATION_NEEDED_COUNT) {
                  BigDecimal factPaymentAmount = satoshiToBtc(tx.getValue(wallet).getValue());
                  bitcoinTransactionService.provideBtcTransaction(invoiceId, txHash, factPaymentAmount, null);
                }
              } catch (final ExecutionException | InterruptedException e) {
                log.error(e);
              } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
              }
            }, pool));
          } catch (IllegalInvoiceAmountException e) {
            log.error(ExceptionUtils.getStackTrace(e));
          }
        }
      });
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }*/
  }
  
  @PreDestroy
  public void preDestroy() {
    try {
      pool.awaitTermination(25, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error(e);
    }
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
  
  @Override
  public String getNewAddress() {
    return kit.wallet().freshReceiveAddress().toBase58();
  }
  
  @Override
  public void backupWallet() {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public BtcWalletInfoDto getWalletInfo() {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations) {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public List<BtcTransactionHistoryDto> listAllTransactions() {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public BigDecimal estimateFee(int blockCount) {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public BigDecimal getActualFee() {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public void setTxFee(BigDecimal fee) {
    throw new NotImplimentedMethod("Not implemented");
  
  }
  
  @Override
  public void submitWalletPassword(String password) {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public String sendToAddress(String address, BigDecimal amount) {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public String sendToAddressAuto(String address, BigDecimal amount) {
    throw new NotImplimentedMethod("Not implemented");
  }
  
  @Override
  public String sendToMany(Map<String, BigDecimal> payments) {
    throw new NotImplimentedMethod("Not implemented");
  }
}
