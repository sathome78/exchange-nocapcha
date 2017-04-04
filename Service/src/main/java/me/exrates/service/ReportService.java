package me.exrates.service;

import me.exrates.model.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ReportService {
  List<InvoiceReportDto> getInvoiceReport(String requesterUserEmail, String startDate, String endDate, String businessRole, String direction, List<String> currencyList);

  List<SummaryInOutReportDto> getUsersSummaryInOutList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);

  Map<String, UserSummaryTotalInOutDto> getUsersSummaryInOutMap(List<SummaryInOutReportDto> resultList);

  List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);

  List<UserSummaryOrdersDto> getUserSummaryOrdersList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);
}