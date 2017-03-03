package me.exrates.service;

import me.exrates.model.dto.InvoiceReportDto;

import java.util.List;

public interface ReportService {
  List<InvoiceReportDto> getInvoiceReport(String requesterUserEmail, String startDate, String endDate, String businessRole, String direction, List<String> currencyList);
}