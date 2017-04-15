package me.exrates.dao;

import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.OperationUserDto;
import me.exrates.model.dto.RefillRequestCreateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * created by ValkSam
 */
public interface RefillRequestDao {

  int findActiveRequestsByMerchantIdAndUserIdForCurrentDate(Integer merchantId, Integer userId);

  int create(RefillRequestCreateDto request);

  List<InvoiceBank> findInvoiceBanksByCurrency(Integer currencyId);

  Optional<LocalDateTime> getAndBlockByIntervalAndStatus(Integer merchantId, Integer currencyId, Integer intervalHours, List<Integer> statusIdList);

  void setNewStatusByDateIntervalAndStatus(Integer merchantId, Integer currencyId, LocalDateTime boundDate, Integer intervalHours, Integer newStatusId, List<Integer> statusIdList);

  List<OperationUserDto> findInvoicesListByStatusChangedAtDate(Integer merchantId, Integer currencyId, Integer statusId, LocalDateTime dateWhenChanged);
}
