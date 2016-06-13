package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface OrderDao {

    int createOrder(ExOrder order);

    ExOrder getOrderById(int orderid);

    boolean setStatus(int orderId, OrderStatus status);

    boolean updateOrder(ExOrder exOrder);

    List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, String email, Locale locale);

    List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, String email, Locale locale);

    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval);

    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale);

    List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs(Locale locale);

    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval);

    OrderInfoDto getOrderInfo(int orderId);

    Object deleteOrderByAdmin(int orderId);

    int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);

    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair, Locale locale);

    OrderCommissionsDto getCommissionForOrder();

    List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType,
                                                Integer offset, Integer limit, Locale locale);

    OrderCreateDto getMyOrderById(int orderId);

    List<OrderWideListDto> getOrdersForAccept(String email, CurrencyPair currencyPair,
                                              OperationType operationType,
                                              Integer offset, Integer limit, Locale locale);

    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                    OperationType operationType);

    boolean lockOrdersListForAcception(List<Integer> ordersList);
}
