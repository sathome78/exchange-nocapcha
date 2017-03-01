package me.exrates.service;

import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.enums.BusinessUserRoleEnum;

import java.util.List;

public interface ReportService {
  List<InvoiceReportDto> getInvoiceReport(String startDate, String endDate, String role, String direction, List<String> currencyList);
}