package me.exrates.service.impl;

import me.exrates.dao.InvoiceRequestDao;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ValkSam
 */
public class ReportServiceImpl implements ReportService {
  @Autowired
  InvoiceRequestDao invoiceRequestDao;

  @Override
  @Transactional
  public List<InvoiceReportDto> getInvoiceReport(String startDate, String endDate, String role, String direction, List<String> currencyList) {
    //TODO check currency list
    direction = InvoiceOperationDirection.valueOf(direction).name();
    List<Integer> roleIdList = BusinessUserRoleEnum.getRealUserRoleIdList(role);
    List<InvoiceRequest> invoiceRequestList = invoiceRequestDao.findAllByUserNameAndDateIntervalAndRoleAndCurrency(startDate, endDate, roleIdList, direction, currencyList);
    //TODO аналогично собрать для биткоинов и для вывода, потом склеить
    return null;
  }
}
