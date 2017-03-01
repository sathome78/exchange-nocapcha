package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
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
public class InvoiceRequestJob {

  @Value("${ir.invoiceTimeOutIntervalMinutes}")
  private Integer EXPIRE_CLEAN_INTERVAL_MINUTES;

  @Autowired
  InvoiceService invoiceService;

  //  @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 5)
  @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 1
  )
  private void invoiceExpiredClean() throws Exception {
    if (EXPIRE_CLEAN_INTERVAL_MINUTES > 0) {
      log.debug("\nstart expired invoices cleaning ... ");
      Integer expireCount = invoiceService.clearExpiredInvoices(EXPIRE_CLEAN_INTERVAL_MINUTES);
      log.debug("\n... end expired invoices cleaning. Mark as expired: " + expireCount);
    }
  }

}



