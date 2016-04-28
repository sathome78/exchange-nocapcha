package me.exrates.dao;

import java.math.BigDecimal;
import java.util.Locale;


public interface DashboardDao {
    BigDecimal getBalanceByCurrency(int userId, int currencyId);
}
