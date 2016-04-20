package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface DashboardDao {

    List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair);

    BigDecimal getBalanceByCurrency(int userId, int currencyId);
}
