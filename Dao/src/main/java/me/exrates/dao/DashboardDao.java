package me.exrates.dao;

import java.math.BigDecimal;


public interface DashboardDao {
    BigDecimal getBalanceByCurrency(int userId, int currencyId);
}
