package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WithdrawData;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ValkSam
 */
public interface WithdrawService {

  Map<String, String> createWithdrawalRequest(CreditsOperation creditsOperation, WithdrawData withdrawData, String userEmail,Locale locale);

  void autoPostWithdrawalRequest(WithdrawRequestPostDto withdrawRequest);

  void postWithdrawalRequest(int requestId, Integer requesterAdminId);

  Map<String, String> withdrawRequest(CreditsOperation creditsOperation, WithdrawData withdrawData, String userEmail, Locale locale);

  List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);

  void setAutoWithdrawParams(MerchantCurrencyOptionsDto merchantCurrencyOptionsDto);

  MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId);

  DataTable<List<WithdrawRequestsAdminTableDto>> getWithdrawRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String authorizedUserEmail, Locale locale);

  WithdrawRequestsAdminTableDto getWithdrawRequestById(Integer id, String authorizedUserEmail);

  List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale);
  
  List<MyInputOutputHistoryDto> getMyInputOutputHistory(
          String email,
          Integer offset, Integer limit,
          Locale locale);
  
  void revokeWithdrawalRequest(int requestId);

  void takeInWorkWithdrawalRequest(int requestId, Integer requesterAdminId);

  void returnFromWorkWithdrawalRequest(int requestId, Integer requesterAdminId);

  void declineWithdrawalRequest(int requestId, Integer requesterAdminId, String comment);

  void confirmWithdrawalRequest(int requestId, Integer requesterAdminId);

  void setAllAvailableInPostingStatus();

  List<WithdrawRequestPostDto> dirtyReadForPostByStatusList(InvoiceStatus status);
}
