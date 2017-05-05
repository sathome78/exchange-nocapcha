package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.model.dto.PendingPaymentFlatDto;
import me.exrates.model.dto.PendingPaymentSimpleDto;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BitcoinService extends IMerchantService {

    int CONFIRMATION_NEEDED_COUNT = 4;

    PendingPayment createInvoice(CreditsOperation operation);

    void provideTransaction(Integer id, String hash, BigDecimal amount, String acceptanceUserEmail) throws Exception;

    List<PendingPaymentFlatDto> getBitcoinTransactions();

    List<PendingPaymentFlatDto> getBitcoinTransactionsForCurrencyPermitted(Integer requesterUserId);
  
  @Transactional(readOnly = true)
  List<PendingPaymentFlatDto> getBitcoinTransactionsAcceptedForCurrencyPermitted(Integer requesterUserId);
  
  Integer getPendingPaymentStatusByInvoiceId(Integer invoiceId);

    Integer clearExpiredInvoices(Integer intervalMinutes) throws Exception;

    void revoke(Integer pendingPaymentId) throws Exception;

    PendingPaymentSimpleDto getPendingPaymentSimple(Integer pendingPaymentId) throws Exception;
  
  @Transactional
  Map<String, String> prepareBitcoinPayment(Payment payment, String email, String currencyNameForQr, Locale locale);
}
