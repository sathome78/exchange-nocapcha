package me.exrates.service.impl;

import me.exrates.dao.DashboardDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import me.exrates.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    DashboardDao dashboardDao;

    @Override
    public Order getLastClosedOrder(CurrencyPair currencyPair){
        return dashboardDao.getLastClosedOrder(currencyPair);
    }

    @Override
    public List<Order> getAllBuyOrders(CurrencyPair currencyPair){
        return dashboardDao.getAllBuyOrders(currencyPair);
    }

    @Override
    public List<Order> getAllSellOrders(CurrencyPair currencyPair){
        return dashboardDao.getAllSellOrders(currencyPair);
    }

    @Override
    public List<Map<String, BigDecimal>> getAmountsFromClosedOrders(CurrencyPair currencyPair){
        return dashboardDao.getAmountsFromClosedOrders(currencyPair);
    }

    @Override
    public List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair){
        return dashboardDao.getDataForChart(currencyPair);
    }

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId){
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

    @Override
    public BigDecimal getMinPriceByCurrency(CurrencyPair currencyPair){
        return dashboardDao.getMinPriceByCurrency(currencyPair);
    }

    @Override
    public BigDecimal getMaxPriceByCurrency(CurrencyPair currencyPair){
        return dashboardDao.getMaxPriceByCurrency(currencyPair);
    }
}
