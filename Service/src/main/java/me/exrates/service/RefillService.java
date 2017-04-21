package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.vo.InvoiceConfirmData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ValkSam
 */
public interface RefillService {

  Map<String, String> createRefillRequest(RefillRequestCreateDto requestCreateDto, Locale locale);

  void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale);

  RefillRequestFlatDto getFlatById(Integer id);

  void revokeRefillRequest(int requestId);

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  Map<String, String> correctAmountAndCalculateCommission(BigDecimal amount, String currency, String merchant);

  Integer clearExpiredInvoices() throws Exception;
}
