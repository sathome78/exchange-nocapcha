package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.BitcoinService;
import me.exrates.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by ValkSam
 */
@Service
@PropertySource(value = {"classpath:/job.properties"})
@Log4j2
public class btcInvoiceRequestJob {

  @Value("${btcInvoice.invoiceTimeOutIntervalMinutes}")
  private Integer EXPIRE_CLEAN_INTERVAL_MINUTES;

  @Autowired
  BitcoinService bitcoinService;

  @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 6)
  private void invoiceExpiredClean() throws Exception {
    if (EXPIRE_CLEAN_INTERVAL_MINUTES > 0) {
      log.debug("\nstart expired btc invoices cleaning ... ");
      Integer expireCount = bitcoinService.clearExpiredInvoices(EXPIRE_CLEAN_INTERVAL_MINUTES);
      log.debug("\n... end expired btc invoices cleaning. Mark as expired: " + expireCount);
    }
  }

}



