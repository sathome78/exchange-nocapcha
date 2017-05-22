package me.exrates.dao;

import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.InvoiceBank;
import me.exrates.model.PagingData;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.InvoiceConfirmData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * created by ValkSam
 */
public interface RefillRequestDao {

  Optional<Integer> findIdByAddressAndMerchantIdAndCurrencyIdAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList);

  Optional<Integer> findIdWithoutConfirmationsByAddressAndMerchantIdAndCurrencyIdAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList);

  Optional<Integer> findIdByAddressAndMerchantIdAndCurrencyIdAndHash(String address, Integer merchantId, Integer currencyId, String hash);

  List<RefillRequestFlatDto> findAllWithoutConfirmationsByMerchantIdAndCurrencyIdAndStatusId(Integer merchantId, Integer currencyId, List<Integer> statusList);

  List<RefillRequestFlatDto> findAllWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(Integer merchantId, Integer currencyId, List<Integer> statusIdList);

  Integer getCountByMerchantIdAndCurrencyIdAndAddressAndStatusId(String address, Integer merchantId, Integer currencyId, List<InvoiceStatus> statusList);

  Optional<Integer> findUserIdByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId);

  Optional<Integer> create(RefillRequestCreateDto request);

  Optional<String> findLastAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId);

  void setStatusById(Integer id, InvoiceStatus newStatus);

  void setStatusAndConfirmationDataById(Integer id, InvoiceStatus newStatus, InvoiceConfirmData invoiceConfirmData);

  List<InvoiceBank> findInvoiceBankListByCurrency(Integer currencyId);

  Optional<LocalDateTime> getAndBlockByIntervalAndStatus(Integer merchantId, Integer currencyId, Integer intervalHours, List<Integer> statusIdList);

  Optional<RefillRequestFlatDto> getFlatByIdAndBlock(Integer id);

  Optional<RefillRequestFlatDto> getFlatById(Integer id);

  void setNewStatusByDateIntervalAndStatus(Integer merchantId, Integer currencyId, LocalDateTime boundDate, Integer intervalHours, Integer newStatusId, List<Integer> statusIdList);

  List<OperationUserDto> findListByMerchantIdAndCurrencyIdStatusChangedAtDate(Integer merchantId, Integer currencyId, Integer statusId, LocalDateTime dateWhenChanged);

  PagingData<List<RefillRequestFlatDto>> getPermittedFlatByStatus(List<Integer> statusIdList, Integer requesterUserId, DataTableParams dataTableParams, RefillFilterData refillFilterData);

  RefillRequestFlatDto getPermittedFlatById(Integer id, Integer requesterUserId);

  RefillRequestFlatAdditionalDataDto getAdditionalDataForId(int id);

  void setHolderById(Integer id, Integer holderId);

  void setRemarkById(Integer id, String remark);

  void setMerchantTransactionIdById(Integer id, String merchantTransactionId) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException;

  boolean checkInputRequests(int currencyId, String email);

  Integer findConfirmationsNumberByRequestId(Integer requestId);

  void setConfirmationsNumberByRequestId(Integer requestId, BigDecimal amount, Integer confirmations);

  Optional<Integer> findUserIdById(Integer requestId);

  List<RefillRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList);
}
