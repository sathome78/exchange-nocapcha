package me.exrates.service.impl;

import lombok.Getter;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;

/**
 * Created by ValkSam
 */
@Service
public class ReportServiceImpl implements ReportService {

  @Autowired
  TransactionService transactionService;

  @Autowired
  MerchantService merchantService;

  @Autowired
  CurrencyService currencyService;

  @Autowired
  UserService userService;

  @Autowired
  UserRoleService userRoleService;

  @Autowired
  WithdrawService withdrawService;

  @Autowired
  RefillService refillService;

  @Autowired
  OrderService orderService;

  @Autowired
  InputOutputService inputOutputService;

  @Override
  @Transactional
  public List<InvoiceReportDto> getInvoiceReport(
      String requesterUserEmail,
      String startDate,
      String endDate,
      String businessRole,
      String direction,
      List<String> currencyList) {
    AvailableCurrencies availableCurrencies = new AvailableCurrencies(requesterUserEmail, currencyList);
    List<Integer> currencyListForRefillOperation = availableCurrencies.getCurrencyListForRefillOperation();
    List<Integer> currencyListForWithdrawOperation = availableCurrencies.getCurrencyListForWithdrawOperation();
    /**/
    direction = "ANY".equals(direction) ? "" : InvoiceOperationDirection.valueOf(direction).name();
    List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
    /**/
    List<InvoiceReportDto> result = new ArrayList<>();
    /**/
    if ((StringUtils.isEmpty(direction) || InvoiceOperationDirection.valueOf(direction) == WITHDRAW)
        && !currencyListForWithdrawOperation.isEmpty()) {
      List<WithdrawRequestFlatForReportDto> withdrawRequestList = withdrawService.findAllByDateIntervalAndRoleAndCurrency(
          startDate, endDate, realRoleIdList, currencyListForWithdrawOperation);
      result.addAll(withdrawRequestList.stream()
          .map(InvoiceReportDto::new)
          .collect(Collectors.toList()));
    }
    if ((StringUtils.isEmpty(direction) || InvoiceOperationDirection.valueOf(direction) == REFILL)
        && !currencyListForRefillOperation.isEmpty()) {
      List<RefillRequestFlatForReportDto> refillRequestList = refillService.findAllByDateIntervalAndRoleAndCurrency(
          startDate, endDate, realRoleIdList, currencyListForWithdrawOperation);
      result.addAll(refillRequestList.stream()
          .map(InvoiceReportDto::new)
          .collect(Collectors.toList()));
    }
    return result.stream()
        .sorted((a, b) -> a.getCreationDate().compareTo(b.getCreationDate()))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<SummaryInOutReportDto> getUsersSummaryInOutList(
      String requesterUserEmail,
      String startDate,
      String endDate,
      String businessRole,
      List<String> currencyList) {
    List<InvoiceReportDto> result = getInvoiceReport(
        requesterUserEmail,
        startDate,
        endDate,
        businessRole,
        "ANY",
        currencyList
    )
        .stream()
        .filter(e -> e.getStatusEnum() == null ? "PROVIDED".equals(e.getStatus()) : e.getStatusEnum().isSuccessEndStatus())
        .collect(Collectors.toList());
    return result.stream()
        .map(SummaryInOutReportDto::new)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, UserSummaryTotalInOutDto> getUsersSummaryInOutMap(List<SummaryInOutReportDto> resultList) {
    Map<String, UserSummaryTotalInOutDto> resultMap = new HashMap<String, UserSummaryTotalInOutDto>() {
      @Override
      public UserSummaryTotalInOutDto put(String key, UserSummaryTotalInOutDto value) {
        if (this.get(key) == null) {
          return super.put(key, value);
        } else {
          UserSummaryTotalInOutDto storedValue = this.get(key);
          storedValue.setTotalIn(storedValue.getTotalIn().add(value.getTotalIn()));
          storedValue.setTotalOut(storedValue.getTotalOut().add(value.getTotalOut()));
          return super.put(key, storedValue);
        }
      }
    };
    resultList.forEach(e -> resultMap.put(
        e.getCurrency(),
        new UserSummaryTotalInOutDto(e.getCurrency(), StringUtils.isEmpty(e.getCreationDateIn()) ? BigDecimal.ZERO : e.getAmount(), StringUtils.isEmpty(e.getCreationDateOut()) ? BigDecimal.ZERO : e.getAmount())
    ));
    return resultMap;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(
      String requesterUserEmail,
      String startDate,
      String endDate,
      String businessRole,
      List<String> currencyList) {
    Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
    List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
    return transactionService.getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(requesterUserId, startDate, endDate, realRoleIdList);
  }


  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryOrdersDto> getUserSummaryOrdersList(
      String requesterUserEmail,
      String startDate,
      String endDate,
      String businessRole,
      List<String> currencyList) {
    Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
    List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
    return transactionService.getUserSummaryOrdersList(requesterUserId, startDate, endDate, realRoleIdList);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(
      String requesterUserEmail,
      String startDate,
      String endDate,
      String businessRole) {
    Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
    List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
    return orderService.getUserSummaryOrdersByCurrencyPairList(requesterUserId, startDate, endDate, realRoleIdList);
  }

  @Getter
  private class AvailableCurrencies {
    private String requesterUserEmail;
    private List<String> currencyList;
    private List<Integer> currencyListForRefillOperation;
    private List<Integer> currencyListForWithdrawOperation;

    AvailableCurrencies(String requesterUserEmail, List<String> currencyList) {
      this.requesterUserEmail = requesterUserEmail;
      this.currencyList = currencyList;
      init();
    }

    private void init() {
      Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
    /**/
      List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList = currencyService.getCurrencyPermittedOperationList(requesterUserId);
      if (currencyList.contains("ALL")) {
        currencyList.clear();
        currencyList.add("ALL");
      }
      currencyListForRefillOperation = userCurrencyOperationPermissionDtoList.stream()
          .filter(e -> e.getInvoiceOperationDirection() == REFILL
              && (currencyList.contains("ALL") || currencyList.contains(e.getCurrencyName())))
          .map(UserCurrencyOperationPermissionDto::getCurrencyId)
          .collect(Collectors.toList());
      currencyListForWithdrawOperation = userCurrencyOperationPermissionDtoList.stream()
          .filter(e -> e.getInvoiceOperationDirection() == WITHDRAW
              && (currencyList.contains("ALL") || currencyList.contains(e.getCurrencyName())))
          .map(UserCurrencyOperationPermissionDto::getCurrencyId)
          .collect(Collectors.toList());
    }
  }

  @Override
  public List<OperationViewDto> getTransactionsHistory(
      String requesterUserEmail,
      Integer userId,
      AdminTransactionsFilterData filterData) {
    Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
    String sortColumn = "TRANSACTION.datetime";
    String sortDirection = "DESC";
    DataTableParams dataTableParams = DataTableParams.sortNoPaginationParams(sortColumn, sortDirection);
    DataTable<List<OperationViewDto>> history = transactionService.showUserOperationHistory(
        requesterUserId,
        userId,
        filterData,
        dataTableParams,
        Locale.ENGLISH);
    return history.getData();
  }

  @Override
  public List<UserIpReportDto> getUserIpReport(String businessRole) {
    List<Integer> realUserRoleIds = StringUtils.isEmpty(businessRole) ? Collections.emptyList() :
            userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);

    return userService.getUserIpReportForRoles(realUserRoleIds);
  }

  @Override
  public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForRealMoneyUsers(LocalDateTime startTime, LocalDateTime endTime) {
      List<UserRole> realMoneyUsengRoles = userRoleService.getRolesUsingRealMoney();
      return orderService.getCurrencyPairTurnoverForPeriod(startTime, endTime, realMoneyUsengRoles.stream()
              .map(UserRole::getRole).collect(Collectors.toList()));
  }

    @Override
    public List<CurrencyInputOutputSummaryDto> getCurrencyTurnoverForRealMoneyUsers(LocalDateTime startTime, LocalDateTime endTime) {
        List<UserRole> realMoneyUsengRoles = userRoleService.getRolesUsingRealMoney();
        return inputOutputService.getInputOutputSummary(startTime, endTime, realMoneyUsengRoles.stream()
                .map(UserRole::getRole).collect(Collectors.toList()));
    }

}
