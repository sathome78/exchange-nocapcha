package me.exrates.service.impl;

import me.exrates.model.InvoiceRequest;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.PendingPaymentFlatForReportDto;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;

/**
 * Created by ValkSam
 */
@Service
public class ReportServiceImpl implements ReportService {
  @Autowired
  InvoiceService invoiceService;

  @Autowired
  PendingPaymentService pendingPaymentService;

  @Autowired
  TransactionService transactionService;

  @Autowired
  CurrencyService currencyService;

  @Autowired
  UserService userService;

  @Override
  @Transactional
  public List<InvoiceReportDto> getInvoiceReport(
      String requesterUserEmail,
      String startDate,
      String endDate,
      String businessRole,
      String direction,
      List<String> currencyList) {
    Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
    /**/
    List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList = currencyService.getCurrencyPermittedOperationList(requesterUserId);
    if (currencyList.contains("ALL")) {
      currencyList.clear();
      currencyList.add("ALL");
    }
    List<Integer> currencyListForRefillOperation = userCurrencyOperationPermissionDtoList.stream()
        .filter(e -> e.getInvoiceOperationDirection() == REFILL
            && (currencyList.contains("ALL") || currencyList.contains(e.getCurrencyName())))
        .map(UserCurrencyOperationPermissionDto::getCurrencyId)
        .collect(Collectors.toList());
    List<Integer> currencyListForWithdrawOperation = userCurrencyOperationPermissionDtoList.stream()
        .filter(e -> e.getInvoiceOperationDirection() == WITHDRAW
            && (currencyList.contains("ALL") || currencyList.contains(e.getCurrencyName())))
        .map(UserCurrencyOperationPermissionDto::getCurrencyId)
        .collect(Collectors.toList());
    /**/
    direction = "ANY".equals(direction) ? "" : InvoiceOperationDirection.valueOf(direction).name();
    List<Integer> realRoleIdList = BusinessUserRoleEnum.getRealUserRoleIdList(businessRole);
    /**/
    List<InvoiceReportDto> result = new ArrayList<>();
    /**/
    if (StringUtils.isEmpty(direction) || InvoiceOperationDirection.valueOf(direction) == REFILL
        && !currencyListForRefillOperation.isEmpty()) {
      /*get list based on the table "invoice_request"
      * Now source_type the INVOICE is source_type that is represented in "invoice_request" */
      List<InvoiceRequest> invoiceRequestList = invoiceService.getByDateIntervalAndRoleAndCurrency(
          startDate, endDate, realRoleIdList, currencyListForRefillOperation);
      result.addAll(invoiceRequestList.stream()
          .map(InvoiceReportDto::new)
          .collect(Collectors.toList()));
      /**/
      /*get list based on the table "pending_payment" for particular source_type, which fully represented in pending_payment
      * Now it is BTC_INVOICE */
      List<String> sourceType = new ArrayList<String>() {{
        add(TransactionSourceType.BTC_INVOICE.name());
      }};
      List<PendingPaymentFlatForReportDto> pendingPaymentList = pendingPaymentService.getByDateIntervalAndRoleAndCurrencyAndSourceType(
          startDate, endDate, realRoleIdList, currencyListForRefillOperation, sourceType);
      result.addAll(pendingPaymentList.stream()
          .map(InvoiceReportDto::new)
          .collect(Collectors.toList()));
      /**/
      /*get list based on the table "transaction" for particular source_type, which is weakly represented in pending_payment or not represented there at all
      * Now source_type the MERCHANT is that which is weakly represented (only not significant fields) in pending_payment or not represented there at all */
      sourceType = new ArrayList<String>() {{
        add(TransactionSourceType.MERCHANT.name());
      }};
      List<TransactionFlatForReportDto> inputTransactionList = transactionService.getAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(
          startDate, endDate, INPUT.getType(), realRoleIdList, currencyListForRefillOperation, sourceType);
      result.addAll(inputTransactionList.stream()
          .map(InvoiceReportDto::new)
          .collect(Collectors.toList()));
    }
    /**/
    //TODO аналогично собрать для биткоинов и для вывода, потом склеить
    return result.stream()
        .sorted((a,b)->a.getCreationDate().compareTo(b.getCreationDate()))
        .collect(Collectors.toList());
  }
}
