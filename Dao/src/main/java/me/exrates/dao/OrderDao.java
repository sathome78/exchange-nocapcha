package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    int createOrder(ExOrder order);

    List<OrderWideListDto> getMyOrders(String email, CurrencyPair currencyPair);

    ExOrder getOrderById(int orderid);

    boolean setStatus(int orderId, OrderStatus status);

    List<OrderListDto> getOrdersSell(CurrencyPair currencyPair);

    List<OrderListDto> getOrdersBuy(CurrencyPair currencyPair);

    boolean updateOrder(ExOrder exOrder);

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair);

    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval);

    OrderInfoDto getOrderInfo(int orderId);

    Integer deleteOrderByAdmin(int orderId);

    int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);


}
