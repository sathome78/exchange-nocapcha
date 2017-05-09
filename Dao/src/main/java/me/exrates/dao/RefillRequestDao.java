package me.exrates.dao;

import me.exrates.model.InvoiceBank;
import me.exrates.model.PagingData;
import me.exrates.model.dto.OperationUserDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatAdditionalDataDto;
import me.exrates.model.dto.RefillRequestFlatDto;
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

  Optional<Integer> findIdByMerchantIdAndCurrencyIdAndAddressAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList);

  Optional<Integer> findIdWithoutConfirmationsByMerchantIdAndCurrencyIdAndAddressAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList);

  Optional<Integer> findIdByMerchantIdAndCurrencyIdAndAddressAndHash(String address, Integer merchantId, Integer currencyId, String hash);

  List<RefillRequestFlatDto> findAllWithoutConfirmationsByMerchantIdAndCurrencyIdAndStatusId(Integer merchantId, Integer currencyId, List<Integer> statusList);

  List<RefillRequestFlatDto> findAllWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(Integer merchantId, Integer currencyId, List<InvoiceStatus> statusList);

  Integer getCountByMerchantIdAndCurrencyIdAndAddressAndStatusId(String address, Integer merchantId, Integer currencyId, List<InvoiceStatus> statusList);

  Optional<Integer> findUserIdByMerchantIdAndCurrencyIdAndAddress(String address, Integer merchantId, Integer currencyId);

  int create(RefillRequestCreateDto request);

  void setStatusById(Integer id, InvoiceStatus newStatus);

  void setStatusAndConfirmationDataById(Integer id, InvoiceStatus newStatus, InvoiceConfirmData invoiceConfirmData);

  List<InvoiceBank> findInvoiceBanksByCurrency(Integer currencyId);

  Optional<LocalDateTime> getAndBlockByIntervalAndStatus(Integer merchantId, Integer currencyId, Integer intervalHours, List<Integer> statusIdList);

  Optional<RefillRequestFlatDto> getFlatByIdAndBlock(Integer id);

  Optional<RefillRequestFlatDto> getFlatById(Integer id);

  void setNewStatusByDateIntervalAndStatus(Integer merchantId, Integer currencyId, LocalDateTime boundDate, Integer intervalHours, Integer newStatusId, List<Integer> statusIdList);

  List<OperationUserDto> findInvoicesListByStatusChangedAtDate(Integer merchantId, Integer currencyId, Integer statusId, LocalDateTime dateWhenChanged);

  PagingData<List<RefillRequestFlatDto>> getPermittedFlatByStatus(List<Integer> statusIdList, Integer requesterUserId, DataTableParams dataTableParams, RefillFilterData refillFilterData);

  RefillRequestFlatDto getPermittedFlatById(Integer id, Integer requesterUserId);

  RefillRequestFlatAdditionalDataDto getAdditionalDataForId(int id);

  void setHolderById(Integer id, Integer holderId);

  void setRemarkById(Integer id, String remark);

  void setMerchantTransactionIdById(Integer id, String merchantTransactionId);

  void setHashById(Integer id, String hash);

  boolean checkInputRequests(int currencyId, String email);

  void setAddressById(Integer id, String address);

  Integer findConfirmationsNumberByRequestId(Integer requestId);

  void setConfirmationsNumberByRequestId(Integer requestId, BigDecimal amount, Integer confirmations);
}
