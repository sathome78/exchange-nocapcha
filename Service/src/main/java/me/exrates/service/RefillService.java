package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
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

  Map<String, Object> createRefillRequest(RefillRequestCreateDto requestCreateDto);

  Optional<String> getAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId);

  List<MerchantCurrency> retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies, String userEmail);

  Integer createRefillRequestByFact(RefillRequestAcceptDto request);

  void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale);

  List<RefillRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);

  List<RefillRequestFlatDto> getInPendingByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId);
  
  Optional<Integer> getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(
          String address,
          Integer merchantId,
          Integer currencyId,
          String hash);
  
  Optional<Integer> getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId);
  
  Optional<Integer> getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(
          String address,
          Integer merchantId,
          Integer currencyId);
  
  List<RefillRequestFlatDto> getInExamineByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId);

  Optional<Integer> getUserIdByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId);

  void putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto onBchExamDto) throws RefillRequestAppropriateNotFoundException;

  void setConfirmationCollectedNumber(RefillRequestSetConfirmationsNumberDto confirmationsNumberDto) throws RefillRequestAppropriateNotFoundException;

  void autoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException;

  void autoAcceptRefillEmptyRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException;

  void acceptRefillRequest(RefillRequestAcceptDto requestAcceptDto);

  @Transactional
  void finalizeAcceptRefillRequest(Integer requestId);

  RefillRequestFlatDto getFlatById(Integer id);

  void revokeRefillRequest(int requestId);

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  Map<String, String> correctAmountAndCalculateCommission(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale);

  Integer clearExpiredInvoices() throws Exception;

  DataTable<List<RefillRequestsAdminTableDto>> getRefillRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, RefillFilterData refillFilterData, String authorizedUserEmail, Locale locale);

  boolean checkInputRequestsLimit(int currencyId, String email);

  void takeInWorkRefillRequest(int requestId, Integer requesterAdminId);

  void returnFromWorkRefillRequest(int requestId, Integer requesterAdminId);

  void declineRefillRequest(int requestId, Integer requesterAdminId, String comment);

  Boolean existsUnclosedRefillRequestForAddress(String address, Integer merchantId, Integer currencyId);

  RefillRequestsAdminTableDto getRefillRequestById(Integer id, String authorizedUserEmail);
  
  Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantTransactionId(String address,
                                                                                       String merchantTransactionId,
                                                                                       String merchantName,
                                                                                       String currencyName);
  
  Optional<String> getLastBlockHashForMerchantAndCurrency(Integer merchantId, Integer currencyId);
}
