package me.exrates.service.impl;

import me.exrates.dao.DashboardDao;
import me.exrates.dao.OrderDao;
import me.exrates.service.DashboardService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LogManager.getLogger(DashboardServiceImpl.class);
    @Autowired
    DashboardDao dashboardDao;
    @Autowired
    OrderDao orderDao;

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        logger.info("Begin 'getBalanceByCurrency' method");
        return dashboardDao.getBalanceByCurrency(userId, currencyId);
    }

}
