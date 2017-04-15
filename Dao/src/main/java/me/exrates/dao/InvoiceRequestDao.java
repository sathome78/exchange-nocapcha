package me.exrates.dao;

import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.User;
import me.exrates.model.dto.InvoiceRequestFlatForReportDto;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;

import java.util.List;
import java.util.Optional;

/**
 * Created by ogolv on 26.07.2016.
 */
public interface InvoiceRequestDao {

  void create(InvoiceRequest invoiceRequest, User user);

  void delete(InvoiceRequest invoiceRequest);

  void updateAcceptanceStatus(InvoiceRequest invoiceRequest);

  Optional<InvoiceRequest> findById(int id);

  Integer getStatusById(int id);

  Optional<InvoiceRequest> findByIdAndBlock(int id);

  List<InvoiceRequest> findByStatus(List<Integer> invoiceRequestStatusIdList);

  List<InvoiceRequest> findAll();

  List<InvoiceRequest> findAllForUser(String email);

  InvoiceBank findBankById(Integer bankId);

  void updateConfirmationInfo(InvoiceRequest invoiceRequest);

  void updateReceiptScan(Integer invoiceId, String receiptScanPath);

  void updateInvoiceRequestStatus(Integer invoiceRequestId, InvoiceRequestStatusEnum invoiceRequestStatus);

  List<InvoiceRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);

  List<InvoiceRequest> findByCurrencyPermittedForUser(Integer requesterUserId);

  List<InvoiceRequest> findByStatusAndByCurrencyPermittedForUser(
      List<Integer> invoiceRequestStatusIdList,
      Integer requesterUserId);
}
