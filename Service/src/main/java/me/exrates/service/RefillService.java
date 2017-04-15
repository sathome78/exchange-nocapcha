package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.vo.WithdrawData;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ValkSam
 */
public interface RefillService {

  Map<String, String> createRefillRequest(RefillRequestCreateDto requestCreateDto, Locale locale);

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  Map<String, String> correctAmountAndCalculateCommission(BigDecimal amount, String currency, String merchant);

  Integer clearExpiredInvoices() throws Exception;
}
