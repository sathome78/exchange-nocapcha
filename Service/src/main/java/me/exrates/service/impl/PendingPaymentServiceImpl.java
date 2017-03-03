package me.exrates.service.impl;

import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.PendingPayment;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.PendingPaymentFlatForReportDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;

/**
 * Created by ValkSam
 */
@Service
public class PendingPaymentServiceImpl implements PendingPaymentService {
  @Autowired
  PendingPaymentDao pendingPaymentDao;

  @Override
  @Transactional(readOnly = true)
  public List<PendingPaymentFlatForReportDto> getByDateIntervalAndRoleAndCurrencyAndSourceType(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList,
      List<String> sourceTypeList) {
    return pendingPaymentDao.findAllByDateIntervalAndRoleAndCurrencyAndSourceType(startDate, endDate, roleIdList, currencyList, sourceTypeList);
  }
}
