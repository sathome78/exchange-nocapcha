package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.service.BitcoinService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ValkSam
 */
@Service
@Log4j2
public class RefillRequestJob {

  @Autowired
  RefillService refillService;

  @Autowired
  private MerchantService merchantService;
  @Autowired
  private MerchantServiceContext serviceContext;

  @Scheduled(initialDelay = 180000, fixedDelay = 1000 * 60 * 5)
  private void refillExpiredClean() throws Exception {
    log.debug("\nstart expired refill cleaning ... ");
    Integer expireCount = refillService.clearExpiredInvoices();
    log.debug("\n... end expired refill cleaning. Mark as expired: " + expireCount);
  }

  /**
   * Method for check unprocessed transactions for coins in merchantNames array.
   * Because blocknotify doesn't work correctly (!!! need to check node config and node config properties !!!)
   * During the check processBtcPayment is executed, which create refill request and refill user wallet.
   */
  @Scheduled(initialDelay = 180000, fixedDelay = 1000 * 60 * 5)
  public void refillCheckPaymentsForCoins() {
      String[] merchantNames = new String[]{"QRK", "LBTC", "LPC", "XFC", "DDX", "ABBC", "CBC"};

      for (String coin : merchantNames) {
          getBitcoinServiceByMerchantName(coin).scanForUnprocessedTransactions(null);
      }
  }

  @Scheduled(initialDelay = 180000, fixedDelay = 1000 * 60 * 5)
  public void refillPaymentsForNonSupportedCoins() {
      try {
          String[] merchantNames = new String[]{"Q"};
          for (String merchantName : merchantNames) {
              BitcoinService service = getBitcoinServiceByMerchantName(merchantName);
              List<BtcTransactionHistoryDto> transactions = service.listAllTransactions();

              for (BtcTransactionHistoryDto transaction : transactions) {
                  if (transaction.getConfirmations() >= service.minConfirmationsRefill()) {
                      Map<String, String> params = new LinkedHashMap<>();
                      params.put("txId", transaction.getTxId());
                      params.put("address", transaction.getAddress());
                      getBitcoinServiceByMerchantName(merchantName).processPayment(params);
                  }
              }

          }
      } catch (Exception e){
          log.error(e);
      }
  }

  private BitcoinService getBitcoinServiceByMerchantName(String merchantName) {
    String serviceBeanName = merchantService.findByName(merchantName).getServiceBeanName();
    IMerchantService merchantService = serviceContext.getMerchantService(serviceBeanName);
    if (merchantService == null || !(merchantService instanceof BitcoinService)) {
      throw new RuntimeException(serviceBeanName);
    }
    return (BitcoinService) merchantService;
  }

}



