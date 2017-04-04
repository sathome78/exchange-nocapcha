package me.exrates.service;

import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.SummaryInOutReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryTotalInOutDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ReportService {
  List<InvoiceReportDto> getInvoiceReport(String requesterUserEmail, String startDate, String endDate, String businessRole, String direction, List<String> currencyList);

  List<SummaryInOutReportDto> getUsersSummaryInOutList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);

  @Transactional(readOnly = true)
  Map<String, UserSummaryTotalInOutDto> getUsersSummaryInOutMap(List<SummaryInOutReportDto> resultList);

  @Transactional(readOnly = true)
  List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);
}