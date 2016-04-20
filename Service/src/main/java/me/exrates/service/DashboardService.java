package me.exrates.service;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderListDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface DashboardService {

    ExOrder getLastClosedOrder();

    ExOrder getLastClosedOrderForCurrencyPair(CurrencyPair currencyPair);

    List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair);

    List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair);

    List<Map<String, BigDecimal>> getAmountsFromClosedOrders(CurrencyPair currencyPair);

    List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair);

    BigDecimal getBalanceByCurrency(int userId, int currencyId);

    BigDecimal getMinPriceByCurrency(CurrencyPair currencyPair);

    BigDecimal getMaxPriceByCurrency(CurrencyPair currencyPair);

}
