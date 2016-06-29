package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.*;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface OrderService {

    /**
     * Returns the ID of the newly created and saved in DB order
     * Generates transaction of transferring money from active balance to reserved balance the corresponding wallet
     *
     * @param order OrderCreateDto, that passed from frontend and that will be converted to entity ExOrder to save in DB
     * @return generated ID of the newly created order, or 0 if order was not be created
     */
    int createOrder(OrderCreateDto order);

     /**
     * TODO ADD JAVADOC
     */
    List<OrderWideListDto> getMyOrdersWithState(
            CacheData cacheData,
            String email, CurrencyPair currencyPair, OrderStatus status,
            OperationType operationType,
            Integer offset, Integer limit, Locale locale);

    /**
     * TODO ADD JAVADOC
     */
    public OrderCreateDto getMyOrderById(int orderId);

    /**
     * Returns entity ExOrder by its ID
     *
     * @param orderId
     * @return entity ExOrder for found order, or null if order not found
     */
    ExOrder getOrderById(int orderId);

    /**
     * Sets new status for existing order with given ID.
     *
     * @param orderId
     * @param status
     * @return
     */
    boolean setStatus(int orderId, OrderStatus status);

    /**
     * Accepts the list of orders
     * The method <b>acceptOrdersList</b> is used to accept each orders from <b>ordersList</b>
     * Before the method <b>acceptOrdersList</b will be called, this method tries to lock the <b>ordersList</b>
     * If not success lock, the OrderAcceptionException will be thrown.
     *
     * @param userAcceptorId  is ID of acceptor-user
     * @param ordersList is list the ID of order that must be accepted
     * @param locale  is current locale. Used to generate messages
     */
    void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale);

    /**
     * Accepts the order
     * and generates set of transactions for creator-user and acceptor-user
     * and modifies wallets for users and company
     * If there were errors while accept, errors will be thrown:
     * - NotEnoughUserWalletMoneyException
     * - TransactionPersistException
     * - OrderAcceptionException
     *
     * @param userId  is ID of acceptor-user
     * @param orderId is ID of order that must be accepted
     * @param locale  is current locale. Used to generate messages
     */
    void acceptOrder(int userId, int orderId, Locale locale);

    /**
     * Cancels the order and set status "CANCELLED"
     * Only order with status "OPENED" can be cancelled
     * This method for cancel order by creator-user
     *
     * @param exOrder is the entity ExOrder of order that must be cancelled
     * @return "true" if the order can be cancelled and has been cancelled successfully, "false" in other cases
     */
    boolean cancellOrder(ExOrder exOrder, Locale locale);

    /**
     * Updates order's fields:
     * - user_acceptor_id
     * - status_id
     * - date_acception
     * Used while accepting process the order
     *
     * @param exOrder
     * @return "true" if order was updated successfully, or "false" if not
     */
    boolean updateOrder(ExOrder exOrder);

    /**
     * Returns data for CoinMarketCap API
     *
     * @param currencyPairName
     * @param backDealInterval
     * @return list the CoinmarketApiDto, which consists info about currency pairs according to API
     */
    List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval);

    /**
     * Returns detailed info about the order, including info from related transactions
     *
     * @param orderId is ID the order
     * @return OrderInfoDto containing detailed info about the order
     */
    OrderInfoDto getOrderInfo(int orderId, Locale locale);

    /**
     * Deletes the order by admin.
     * If the order has status "CLOSED" then:
     * - deletes the order and related transactions and corrects the users' wallets and company's wallets
     * If the order has status "OPENED" then:
     * - deletes the order and corrects the user-creator wallet
     * Deleting the order and transactions means setting status "DELETED"
     *
     * @param orderId is ID the order
     * @return 0: no rows were obtained for the deleted_order_id: order has not status OPENED or CLOSED
     * 1: exorder were not be accepted (status "OPENED") - there were no associated transaction
     * n: number of processed rows (including exorders and transaction)
     * throws OrderDeletingException with OrderDeleteStatus in message
     */
    Integer deleteOrderByAdmin(int orderId);

    /**
     * Searches order by its params:
     *
     * @param currencyPair
     * @param orderType
     * @param orderDate
     * @param orderRate
     * @param orderVolume
     * @return ID the found order, or -1 if order with the parameters has not be found
     */
    Integer searchOrderByAdmin(Integer currencyPair, String orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume);

    /**
     * Returns object that contains data with statistics of orders for currencyPair.
     * Statistics formed by data for certain period: from current moment to <i></>backDealInterval</i> back
     * @author ValkSam
     * @param currencyPair
     * @param backDealInterval is the length of interval
     * @return statistics of orders for currencyPair
     */
    ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale);

    /**
     * Returns statistics of orders by currency pairs.
     * Statistics contains last and pred last rates for each currency pair
     * @author ValkSam
     * @return statistics of orders by currency pairs
     */
    List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(CacheData cacheData, Locale locale);

    /**
     * Returns data for candle chart for <i>currencyPair</i> for for period: from current moment to <i></>interval</i> back
     * @author ValkSam
     * @param currencyPair
     * @param interval
     * @return data for candle chart
     */
    List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval);

    /**
     * Returns data for area type chart for <i>currencyPair</i> for for period: from current moment to <i></>interval</i> back
     * @author ValkSam
     * @param currencyPair
     * @param interval
     * @return data for area chart
     */
    List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval interval);

    /**
     * Returns data for the history of accepted orders
     * @param backDealInterval
     * @param limit
     * @param locale
     * @return
     */
    List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(CacheData cacheData, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair, Locale locale);

    /**
     * Returns SELL and BUY commissions for orders
     * @return
     */
    OrderCommissionsDto getCommissionForOrder();

    /**
     * Returns list of Buy orders of status open
     * @param currencyPair
     * @param email is the email of current user
     * @return list of Buy orders
     */
    List<OrderListDto> getAllBuyOrders(CacheData cacheData, CurrencyPair currencyPair, String email, Locale locale);

    /**
     * Returns list of Sell orders of status open, exclude the orders of current user
     * @param currencyPair
     * @param email is the email of current user
     * @return list of Sell orders
     */
    List<OrderListDto> getAllSellOrders(CacheData cacheData, CurrencyPair currencyPair, String email, Locale locale);

    /**
     * Returns data of
     * - userId by email,
     * - wallet by currency id
     * - commission by operation type
     * Used for creation order with corresponding parameters
     * @param email
     * @param currency
     * @param operationType
     * @return
     */
    WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType);
}
