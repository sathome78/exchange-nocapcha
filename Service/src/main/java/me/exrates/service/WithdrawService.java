package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.MerchantCurrencyAutoParamDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WithdrawData;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author ValkSam
 */
public interface WithdrawService {

    Map<String, String> acceptWithdrawalRequest(int requestId, Locale locale, Principal principal);

    Map<String, Object> declineWithdrawalRequest(int requestId, Locale locale, String email);

    List<WithdrawRequest> findAllWithdrawRequests();

    DataTable<List<WithdrawRequest>> findWithdrawRequestsByStatus(Integer requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String userEmail);

    Map<String, String> withdrawRequest(CreditsOperation creditsOperation, WithdrawData withdrawData, String userEmail, Locale locale);


    List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);

    void setAutoWithdrawParams(MerchantCurrencyOptionsDto merchantCurrencyOptionsDto);

    MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId);

    List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale);
}
