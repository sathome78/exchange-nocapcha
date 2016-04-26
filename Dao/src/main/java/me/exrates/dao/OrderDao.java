package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    int createOrder(ExOrder order);

    List<ExOrder> getMyOrders(int userId, CurrencyPair currencyPair);

    List<ExOrder> getAllOpenedOrders();

    ExOrder getOrderById(int orderid);

    boolean setStatus(int orderId, OrderStatus status);

    List<OrderListDto> getOrdersSell(CurrencyPair currencyPair);

    List<OrderListDto> getOrdersBuy(CurrencyPair currencyPair);

    boolean updateOrder(ExOrder exOrder);

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair);

    ExOrder getLastClosedOrder();

    ExOrder getLastClosedOrderForCurrencyPair(CurrencyPair currencyPair);

    BigDecimal getMinExRateByCurrencyPair(CurrencyPair currencyPair);

    BigDecimal getMaxExRateByCurrencyPair(CurrencyPair currencyPair);

    List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair, String period);


}
