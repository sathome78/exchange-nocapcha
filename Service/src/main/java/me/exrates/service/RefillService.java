package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestsAdminTableDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.vo.InvoiceConfirmData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author ValkSam
 */
public interface RefillService {

  Map<String, String> createRefillRequest(RefillRequestCreateDto requestCreateDto, Locale locale);

  void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale);

  Optional<Integer> getActiveRequestIdByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId);

  void autoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto);

  void acceptRefillRequest(RefillRequestAcceptDto requestAcceptDto);

  RefillRequestFlatDto getFlatById(Integer id);

  void revokeRefillRequest(int requestId);

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  Map<String, String> correctAmountAndCalculateCommission(BigDecimal amount, String currency, String merchant);

  Integer clearExpiredInvoices() throws Exception;

  DataTable<List<RefillRequestsAdminTableDto>> getRefillRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, RefillFilterData refillFilterData, String authorizedUserEmail, Locale locale);

  boolean checkInputRequestsLimit(int currencyId, String email);

  void revokeWithdrawalRequest(int requestId);

  void takeInWorkRefillRequest(int requestId, Integer requesterAdminId);

  void returnFromWorkRefillRequest(int requestId, Integer requesterAdminId);

  void declineRefillRequest(int requestId, Integer requesterAdminId, String comment);
}
