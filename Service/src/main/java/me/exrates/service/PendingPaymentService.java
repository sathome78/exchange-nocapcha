package me.exrates.service;

import me.exrates.model.dto.PendingPaymentFlatForReportDto;

import java.util.List;

/**
 * Created by ValkSam on 06.03.2017.
 */
public interface PendingPaymentService {
  List<PendingPaymentFlatForReportDto> getByDateIntervalAndRoleAndCurrencyAndSourceType(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList, List<String> sourceTypeList);
}
