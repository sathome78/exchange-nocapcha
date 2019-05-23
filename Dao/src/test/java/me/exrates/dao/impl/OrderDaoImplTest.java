package me.exrates.dao.impl;

import config.DataComparisonTest;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import config.AbstractDatabaseContextTest;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.enums.OrderStatus;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrderDaoImplTest.InnerConf.class})
public class OrderDaoImplTest extends DataComparisonTest {

    private final String TABLE = "EXORDERS";

    @Autowired
    private OrderDao orderDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE);
            String sql = "INSERT INTO EXORDERS" +
                    "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
                    "  VALUES " +
                    "  (1, 1, 1, 0.5, 1, 0.5, 1, 0.01, 1, 1, \'LIMIT\')";
            prepareTestData(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createOrder_Success() {
        String sql = "SELECT * FROM " + TABLE;

        around()
                .withSQL(sql)
                .run(() -> orderDao.createOrder(getTestOrder()));
    }

    @Test
    public void getOrderById_Ok() {
        ExOrder order = orderDao.getOrderById(1);
        assertEquals(1, order.getId());
    }

    @Test
    public void getOrderById_NotFound() {
        ExOrder order = orderDao.getOrderById(2);
        assertNull(order);
    }

    private ExOrder getTestOrder() {
        ExOrder order = new ExOrder();
        order.setUserId(1);
        order.setCurrencyPairId(1);
        order.setOperationType(OperationType.BUY);
        order.setExRate(BigDecimal.TEN);
        order.setAmountBase(BigDecimal.ONE);
        order.setAmountConvert(BigDecimal.TEN);
        order.setComissionId(1);
        order.setCommissionFixedAmount(BigDecimal.ZERO);
        order.setUserAcceptorId(2);
        order.setDateCreation(LocalDateTime.now());
        order.setDateAcception(LocalDateTime.now());
        order.setStatus(OrderStatus.OPENED);
        order.setCurrencyPair(null);
        order.setSourceId(1);
        order.setOrderBaseType(OrderBaseType.LIMIT);
        order.setTradeId(1L);
        order.setEvent(OrderEventEnum.CREATE);
        return order;
    }

    @Configuration
    static class InnerConf extends AppContextConfig {

        @Override
        protected String getSchema() {
            return "OrderDaoImplTest";
        }

        @Bean
        public OrderDao orderDao() {
            return new OrderDaoImpl();
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
