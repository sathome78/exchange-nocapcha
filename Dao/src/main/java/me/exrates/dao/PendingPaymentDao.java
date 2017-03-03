package me.exrates.dao;

import me.exrates.model.PendingPayment;
import me.exrates.model.dto.PendingPaymentFlatDto;
import me.exrates.model.dto.PendingPaymentFlatForReportDto;
import me.exrates.model.dto.PendingPaymentSimpleDto;
import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PendingPaymentDao {

  void create(PendingPayment pendingPayment);

  List<PendingPaymentSimpleDto> findAllByHash(String hash);

  Optional<PendingPayment> findByInvoiceId(Integer invoiceId);

  void setStatusById(Integer invoiceId, Integer newStatus);

  Optional<PendingPaymentStatusDto> setStatusAndHashByAddressAndStatus(String address, Integer currentStatus, Integer newStatus, String hash);

  Optional<PendingPaymentSimpleDto> findByAddressAndNotProvided(String address);

  boolean existsPendingPaymentWithAddressAndStatus(String address, List<Integer> paymentStatusIdList);

  void delete(int invoiceId);

  Optional<PendingPayment> findByIdAndBlock(Integer invoiceId);

  void updateAcceptanceStatus(PendingPayment pendingPayment);

  Integer getStatusById(int id);

  List<PendingPaymentFlatDto> findFlattenDtoByStatus(String sourceName, List<Integer> pendingPaymentStatusIdList);

  Optional<LocalDateTime> getAndBlockBySourceTypeAndIntervalAndStatus(String sourceName, Integer intervalMinutes, List<Integer> invoiceRequestStatusIdList);

  void setNewStatusBySourceTypeAndDateIntervalAndStatus(String sourceName, LocalDateTime nowDate, Integer intervalMinutes, Integer newInvoiceRequestStatusId, List<Integer> invoiceRequestStatusIdList);

  List findInvoicesListBySourceTypeAndStatusChangedAtDate(String sourceName, Integer invoiceRequestStatusId, LocalDateTime dateWhenChanged);

  Optional<PendingPaymentSimpleDto> findById(Integer pendingPaymentId);

  List<PendingPaymentFlatForReportDto> findAllByDateIntervalAndRoleAndCurrencyAndSourceType(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList,
      List<String> sourceTypeList);
}
