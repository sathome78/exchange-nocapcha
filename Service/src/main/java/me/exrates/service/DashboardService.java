package me.exrates.service;

import java.math.BigDecimal;


public interface DashboardService {

    BigDecimal getBalanceByCurrency(int userId, int currencyId);

}
