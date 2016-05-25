package me.exrates.service;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;

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
     * Returns TWO list the information of user orders: BUY and SELL.
     * These lists used for display user's orders on the MyOrders page
     *
     * @param email        used for identifacation the user
     * @param currencyPair is the current currency pair for which orders will be included in the MyOrders lists.
     *                     For all currency pairs if null
     * @param locale       is current locale. It's nessary to desplay title of order status
     * @return map of two list of user's orders with keys "sell" and "buy"
     */
    Map<String, List<OrderWideListDto>> getMyOrders(String email, CurrencyPair currencyPair, Locale locale);

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
     * Returns list all orders with type "SELL" and status "OPENED"
     *
     * @param currencyPair is the current currency pair for which orders will be included in the list.
     *                     For all currency pairs if null
     * @return list the OrderListDto for all orders with type "SELL" and status "OPENED"
     */
    List<OrderListDto> getOrdersSell(CurrencyPair currencyPair);

    /**
     * Returns list all orders with type "BUY" and status "OPENED"
     *
     * @param currencyPair is the current currency pair for which orders will be included in the list.
     *                     For all currency pairs if null
     * @return list the OrderListDto for all orders with type "BUY" and status "OPENED"
     */
    List<OrderListDto> getOrdersBuy(CurrencyPair currencyPair);

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
    OrderInfoDto getOrderInfo(int orderId);

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
}
