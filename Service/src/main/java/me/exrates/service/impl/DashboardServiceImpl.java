package me.exrates.service.impl;

import me.exrates.dao.DashboardDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CandleChartItemDto;
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
import java.util.Locale;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LogManager.getLogger(DashboardServiceImpl.class);
    @Autowired
    DashboardDao dashboardDao;
    @Autowired
    OrderDao orderDao;

    @Override
    public List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair) {
        return orderDao.getOrdersBuyForCurrencyPair(currencyPair);
    }

    @Override
    public List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair) {
        return orderDao.getOrdersSellForCurrencyPair(currencyPair);
    }

    @Override
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval interval) {
        return orderDao.getDataForAreaChart(currencyPair, interval);
    }

    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval) {
        return orderDao.getDataForCandleChart(currencyPair, interval);
    }

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        return orderDao.getOrderStatistic(currencyPair, backDealInterval);
    }

}
