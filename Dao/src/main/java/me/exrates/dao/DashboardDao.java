package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface DashboardDao {

    Order getLastClosedOrder();

    List<Order> getAllBuyOrders(CurrencyPair currencyPair);

    List<Order> getAllSellOrders(CurrencyPair currencyPair);

    List<Map<String, BigDecimal>> getAmountsFromClosedOrders(CurrencyPair currencyPair);

    List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair);

    BigDecimal getBalanceByCurrency(int userId, int currencyId);

    BigDecimal getMinPriceByCurrency(CurrencyPair currencyPair);

    BigDecimal getMaxPriceByCurrency(CurrencyPair currencyPair);
}
