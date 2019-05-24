package me.exrates.dao.impl;

import config.DataComparisonTest;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.enums.OrderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrderDaoImplTest.InnerConf.class})
public class OrderDaoImplTest extends DataComparisonTest {

    private final String TABLE_EXORDERS = "EXORDERS";

    @Autowired
    private OrderDao orderDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE_EXORDERS);
            String sql = "INSERT INTO EXORDERS" +
                    "  (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
                    "  VALUES " +
                    "  (1, 1, 1, 1, 0.5, 1, 0.5, 1, 0.01, 1, 1, \'LIMIT\'), (2, 1, 1, 1, 0.5, 1, 0.5, 1, 0.01, 1, 1, \'LIMIT\')";
            prepareTestData(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createOrder_Success() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

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

    @Test
    public void getOrderById_Has_2_Arguments_Ok() {
        ExOrder order = orderDao.getOrderById(1, 1);
        assertEquals(1, order.getId());
    }

    @Test
    public void getOrderById_Has_2_Arguments_NotFound_Wrong_OrderId() {
        ExOrder order = orderDao.getOrderById(5, 1);
        assertNull(order);
    }

    @Test
    public void getOrderById_Has_2_Arguments_NotFound_Wrong_UserId() {
        ExOrder order = orderDao.getOrderById(1, 5);
        assertNull(order);
    }

    @Test
    public void getOrderById_Has_2_Arguments_NotFound_All_Args_Wrong() {
        ExOrder order = orderDao.getOrderById(5, 5);
        assertNull(order);
    }


    @Test
    public void updateOrder_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        ExOrder order = getTestOrder();
        order.setId(1);
        order.setUserAcceptorId(8);
        order.setStatus(OrderStatus.SPLIT_CLOSED);

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(order));
    }

    @Test
    public void updateOrder_NotUpdate_The_Same_Object() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        ExOrder order = getTestOrder();

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(order));
    }

    @Test
    public void updateOrder_Has_2_Arguments_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int orderId = 1;

        ExOrder order = getTestOrder();
        order.setUserId(8);
        order.setCurrencyPairId(8);
        order.setOperationType(OperationType.MANUAL);
        order.setExRate(BigDecimal.TEN);
        order.setAmountBase(BigDecimal.TEN);
        order.setAmountConvert(BigDecimal.TEN);
        order.setComissionId(8);
        order.setCommissionFixedAmount(BigDecimal.TEN);
        order.setStatus(OrderStatus.SPLIT_CLOSED);
        order.setSourceId(8);
        order.setOrderBaseType(OrderBaseType.ICO);

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(orderId, order));
    }

    @Test
    public void updateOrder_Has_2_Arguments_NotUpdate_The_Same_Object() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int orderId = 1;
        ExOrder order = getTestOrder();

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(orderId, order));
    }

    @Test
    public void postAcceptedOrderToDB_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        around()
                .withSQL(sql)
                .run(() -> orderDao.postAcceptedOrderToDB(getTestOrder()));
    }

    @Test
    public void getAllOpenedOrdersByUserId_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int userId = 1;

        around()
                .withSQL(sql)
                .run(() -> orderDao.getAllOpenedOrdersByUserId(userId));
    }

    @Test
    public void getAllOpenedOrdersByUserId_NotFound() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int wrongUserId = 5;

        around()
                .withSQL(sql)
                .run(() -> orderDao.getAllOpenedOrdersByUserId(wrongUserId));
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
