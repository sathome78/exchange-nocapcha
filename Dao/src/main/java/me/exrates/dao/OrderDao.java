package me.exrates.dao;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    int createOrder(ExOrder order);

    List<ExOrder> getMyOrders(int userId, CurrencyPair currencyPair);

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


}
