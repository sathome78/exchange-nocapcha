package me.exrates.service;

import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.vo.BackDealInterval;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public interface DashboardService {

    List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair);

    List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair);

    List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair, BackDealInterval interval);

    BigDecimal getBalanceByCurrency(int userId, int currencyId);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval);
}
