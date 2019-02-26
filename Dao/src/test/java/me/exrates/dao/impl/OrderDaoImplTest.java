package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.configuration.TestConfiguration;
import me.exrates.model.dto.OrderFilterDataDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class, OrderDaoImplTest.InnerConf.class})
public class OrderDaoImplTest {

    @Autowired
    private OrderDao orderDao;

    private OrderFilterDataDto filter;
    private Locale locale;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        locale = Locale.ENGLISH;

        filter = OrderFilterDataDto.builder()
                .userId(13)
                .currencyPair(null)
                .currencyName(StringUtils.EMPTY)
                .status(OrderStatus.CLOSED)
                .scope(StringUtils.EMPTY)
                .offset(0)
                .limit(15)
                .hideCanceled(false)
                .sortedColumns(Collections.emptyMap())
                .build();
    }

    @Test
    public void getMyOrdersWithStateTest_Success() {
        List<OrderWideListDto> ordersHistory = orderDao.getMyOrdersWithState(filter, locale);

        assertNotNull(ordersHistory);
        assertFalse(ordersHistory.isEmpty());
        assertEquals(15, ordersHistory.size());
    }

    @Configuration
    static class InnerConf {

        @Bean
        public OrderDao orderDao() {
            return new OrderDaoImpl();
        }

        @Bean
        public NamedParameterJdbcTemplate masterTemplate() {
            return Mockito.mock(NamedParameterJdbcTemplate.class);
        }

        @Bean
        public NamedParameterJdbcTemplate slaveForReportsTemplate() {
            return Mockito.mock(NamedParameterJdbcTemplate.class);
        }

        @Bean
        public CommissionDao commissionDao() {
            return Mockito.mock(CommissionDao.class);
        }

        @Bean
        public WalletDao walletDao() {
            return Mockito.mock(WalletDao.class);
        }
    }
}
