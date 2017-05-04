package me.exrates.service.stopOrder;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.CacheData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;

/**
 * Created by maks on 20.04.2017.
 */
public interface StopOrderService {

    @Transactional
    String create(OrderCreateDto orderCreateDto, OrderActionEnum actionEnum, Locale locale);

    Integer createOrder(ExOrder exOrder);

    void proceedStopOrders(int pairId, NavigableSet<StopOrderSummaryDto> orders);

    @Transactional
    void proceedStopOrderAndRemove(int stopOrderId);

    List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds);

    @Transactional
    boolean cancelOrder(ExOrder exOrder, Locale locale);

    @Transactional
    boolean setStatus(int orderId, OrderStatus status);

    OrderCreateDto getOrderById(Integer orderId, boolean forUpdate);

    void onLimitOrderAccept(ExOrder exOrder);

    @Async
    void onStopOrderCreate(ExOrder exOrder);

    @Transactional(readOnly = true)
    List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData,
                                                String email, CurrencyPair currencyPair, OrderStatus status,
                                                OperationType operationType,
                                                String scope, Integer offset, Integer limit, Locale locale);

    @Transactional
    DataTable<List<OrderBasicInfoDto>> searchOrdersByAdmin(AdminStopOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale);

    /*@Transactional
    OrderInfoDto getStopOrderInfo(int orderId, Locale locale);*/

    @Transactional
    OrderInfoDto getStopOrderInfo(int orderId, Locale locale);

    Object deleteOrderByAdmin(int id, Locale locale);
}
