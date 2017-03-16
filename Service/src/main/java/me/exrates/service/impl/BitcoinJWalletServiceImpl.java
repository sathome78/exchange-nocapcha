package me.exrates.service.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.BitcoinWalletAppKit;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.service.BitcoinWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Created by OLEG on 16.03.2017.
 */
@Log4j2
@Component("BitcoinJService")
public class BitcoinJWalletServiceImpl implements BitcoinWalletService {
  
  private WalletAppKit kit;
  
  
  @Autowired
  private BitcoinWalletAppKit kitBefore;
  
  @Autowired
  private CurrencyService currencyService;
  
  @Autowired
  private MerchantService merchantService;
  
  @Autowired
  private PendingPaymentDao paymentDao;
  
  
  
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
   /* try {
      InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
      kit.wallet().addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
        final String address = extractRecipientAddress(tx.getOutputs());
        System.out.println(address);
        if (paymentDao.existsPendingPaymentWithAddressAndStatus(address, Arrays.asList(beginStatus.getCode()))) {
          String txHash = tx.getHashAsString();
          PendingPaymentStatusDto pendingPayment = markStartConfirmationProcessing(address, txHash);
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
              log.error(e);
            } catch (Exception e) {
              log.error(ExceptionUtils.getStackTrace(e));
            }
          }, pool));
        }
      });
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }*/
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
}
