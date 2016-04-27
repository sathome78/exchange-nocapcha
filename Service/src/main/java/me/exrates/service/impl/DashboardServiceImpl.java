package me.exrates.service.impl;

import me.exrates.dao.DashboardDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.vo.BackDealInterval;
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

    private static final Logger logger = LogManager.getLogger(DashboardServiceImpl.class);
    @Autowired
    DashboardDao dashboardDao;
    @Autowired
    OrderDao orderDao;

    @Override
    public ExOrder getLastClosedOrder() {
        logger.info("Begin 'getLastClosedOrder' method");
        return orderDao.getLastClosedOrder();
    }

    @Override
    public ExOrder getLastClosedOrderForCurrencyPair(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        logger.info("Begin 'getLastClosedOrder' method");
        return orderDao.getLastClosedOrderForCurrencyPair(currencyPair, backDealInterval);
    }

    @Override
    public List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair) {
        logger.info("Begin 'getAllBuyOrders' method");
        return orderDao.getOrdersBuyForCurrencyPair(currencyPair);
    }

    @Override
    public List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair) {
        logger.info("Begin 'getAllSellOrders' method");
        return orderDao.getOrdersSellForCurrencyPair(currencyPair);
    }

    @Override
    public List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair, BackDealInterval interval) {
        logger.info("Begin 'getDataForChart' method");
        return orderDao.getDataForChart(currencyPair, interval);
    }

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        logger.info("Begin 'getBalanceByCurrency' method");
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

    @Override
    public BigDecimal getMinPriceByCurrency(CurrencyPair currencyPair) {
        logger.info("Begin 'getMinPriceByCurrency' method");
        return orderDao.getMinExRateByCurrencyPair(currencyPair);
    }

    @Override
    public BigDecimal getMaxPriceByCurrency(CurrencyPair currencyPair) {
        logger.info("Begin 'getMaxPriceByCurrency' method");
        return orderDao.getMaxExRateByCurrencyPair(currencyPair);
    }

    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        return orderDao.getOrderStatistic(currencyPair, backDealInterval);
    }
}
