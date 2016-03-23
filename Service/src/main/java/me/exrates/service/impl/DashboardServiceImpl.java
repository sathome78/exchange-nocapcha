package me.exrates.service.impl;

import me.exrates.dao.DashboardDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import me.exrates.service.DashboardService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    DashboardDao dashboardDao;

    private static final Logger logger = LogManager.getLogger(DashboardServiceImpl.class);

    @Override
    public Order getLastClosedOrder(CurrencyPair currencyPair){
        logger.info("Begin 'getLastClosedOrder' method");
        return dashboardDao.getLastClosedOrder(currencyPair);
    }

    @Override
    public List<Order> getAllBuyOrders(CurrencyPair currencyPair){
        logger.info("Begin 'getAllBuyOrders' method");
        return dashboardDao.getAllBuyOrders(currencyPair);
    }

    @Override
    public List<Order> getAllSellOrders(CurrencyPair currencyPair){
        logger.info("Begin 'getAllSellOrders' method");
        return dashboardDao.getAllSellOrders(currencyPair);
    }

    @Override
    public List<Map<String, BigDecimal>> getAmountsFromClosedOrders(CurrencyPair currencyPair){
        logger.info("Begin 'getAmountsFromClosedOrders' method");
        return dashboardDao.getAmountsFromClosedOrders(currencyPair);
    }

    @Override
    public List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair){
        logger.info("Begin 'getDataForChart' method");
        return dashboardDao.getDataForChart(currencyPair);
    }

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId){
        logger.info("Begin 'getBalanceByCurrency' method");
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

    @Override
    public BigDecimal getMinPriceByCurrency(CurrencyPair currencyPair){
        logger.info("Begin 'getMinPriceByCurrency' method");
        return dashboardDao.getMinPriceByCurrency(currencyPair);
    }

    @Override
    public BigDecimal getMaxPriceByCurrency(CurrencyPair currencyPair){
        logger.info("Begin 'getMaxPriceByCurrency' method");
        return dashboardDao.getMaxPriceByCurrency(currencyPair);
    }
}
