package me.exrates.service.impl;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.OrderService;
import me.exrates.service.StopOrderService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.exception.OrderCancellingException;
import me.exrates.service.stopOrder.RatesHolder;
import me.exrates.service.stopOrder.StopOrdersHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;

/**
 * Created by maks on 20.04.2017.
 */
@Service
@Log4j2
public class StopOrderServiceImpl implements StopOrderService {

    @Autowired
    private StopOrderDao stopOrderDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StopOrdersHolder stopOrdersHolder;
    @Autowired
    private RatesHolder ratesHolder;
    @Autowired
    private TransactionDescription transactionDescription;
    @Autowired
    private WalletService walletService;
    @Autowired
    private MessageSource messageSource;

    @Transactional
    @Override
    public Integer createOrder(ExOrder exOrder) {
        StopOrder order = new StopOrder(exOrder);
        int orderId = stopOrderDao.create(order);
        exOrder.setId(orderId);
        this.onStopOrderCreate(exOrder);
        return orderId;
    }


    @Override
    public void proceedStopOrders(int pairId, NavigableSet<StopOrderSummaryDto> orders) {
       orders.forEach(p->{
           try {
               proceedStopOrders(p.getOrderId());
           } catch (Exception e) {
               log.error("error processing stop order {}", e);
           }
       });
    }


    @Transactional
    @Override
    public void proceedStopOrders(int stopOrderId) {
        OrderCreateDto dto = getOrderById(stopOrderId, true);
        if (dto == null || !dto.getStatus().equals(OrderStatus.OPENED)) {
            throw new RuntimeException(String.format(" order %s not found in db or illegal status ", stopOrderId));
        }
        cancelCostsReserveForStopOrder(new ExOrder(dto), Locale.ENGLISH, OrderActionEnum.ACCEPT);
        int orderId = orderService.createOrder(dto, OrderActionEnum.CREATE);
        stopOrderDao.setStatusAndChildOrderId(stopOrderId, orderId, OrderStatus.CLOSED);
        stopOrdersHolder.delete(dto.getCurrencyPair().getId(),
                new StopOrderSummaryDto(stopOrderId, dto.getStop(), dto.getOperationType()));
    }

    @Transactional
    private void cancelCostsReserveForStopOrder(ExOrder dto, Locale locale, OrderActionEnum actionEnum) {
        WalletsForOrderCancelDto walletsForOrderCancelDto = walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                dto.getId(),
                dto.getOperationType());
        OrderStatus currentStatus = OrderStatus.convert(walletsForOrderCancelDto.getOrderStatusId());
        if (currentStatus != OrderStatus.OPENED) {
            throw new OrderAcceptionException(messageSource.getMessage("order.cannotcancel", null, locale));
        }
        WalletTransferStatus transferResult = walletService.walletInnerTransfer(
                walletsForOrderCancelDto.getWalletId(),
                walletsForOrderCancelDto.getReservedAmount(),
                TransactionSourceType.STOP_ORDER,
                walletsForOrderCancelDto.getOrderId(),
                transactionDescription.get(dto.getStatus(), actionEnum));
        if (transferResult != WalletTransferStatus.SUCCESS) {
            throw new OrderCancellingException(transferResult.toString());
        }
    }

    @Override
    public List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds) {
        return stopOrderDao.getOrdersBypairId(pairIds, OrderStatus.OPENED);
    }

    @Override
    @Transactional
    public boolean cancelOrder(ExOrder exOrder, Locale locale) {
        cancelCostsReserveForStopOrder(exOrder, locale, OrderActionEnum.CANCEL);
        boolean res = this.setStatus(exOrder.getId(), OrderStatus.CANCELLED);
        stopOrdersHolder.delete(exOrder.getCurrencyPairId(),
                new StopOrderSummaryDto(exOrder.getId(), exOrder.getExRate()));
        return res;
    }


    @Transactional
    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        return stopOrderDao.setStatus(orderId, status);
    }

    @Override
    public OrderCreateDto getOrderById(Integer orderId, boolean forUpdate) {
        return stopOrderDao.getOrderById(orderId, forUpdate);
    }


    /*check stop orders on order accepted and rates changed*/
    @Async
    @Override
    public void onLimitOrderAccept(ExOrder exOrder) {
        try {
            ratesHolder.onRateChange(exOrder.getCurrencyPairId(), exOrder.getOperationType(), exOrder.getExRate());
            NavigableSet<StopOrderSummaryDto> result;
            switch (exOrder.getOperationType()) {
                case SELL: {
                    synchronized (this) {
                        result = stopOrdersHolder.getSellOrdersForPairAndStopRate(exOrder.getCurrencyPairId(), exOrder.getExRate());
                        if (!result.isEmpty()) {
                            this.proceedStopOrders(exOrder.getCurrencyPairId(), result);
                        }
                    }
                    break;
                }
                case BUY: {
                    synchronized (this) {
                        result = stopOrdersHolder.getBuyOrdersForPairAndStopRate(exOrder.getCurrencyPairId(), exOrder.getExRate());
                        if (!result.isEmpty()) {
                            this.proceedStopOrders(exOrder.getCurrencyPairId(), result);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("cant check stop orders {}", e);
        }
    }


    /*try to accept order after create*/
    @Async
    @Override
    public void onStopOrderCreate(ExOrder exOrder) {
        try {
            BigDecimal currentRate = ratesHolder.getCurrentRate(exOrder.getCurrencyPairId(), exOrder.getOperationType());
            switch (exOrder.getOperationType()) {
                case SELL: {
                    synchronized (this) {
                        if (currentRate!= null && exOrder.getStop().compareTo(currentRate) >= 0) {
                            this.proceedStopOrders(exOrder.getId());
                        } else {
                           stopOrdersHolder.addOrder(exOrder);
                        }
                    }
                    break;
                }
                case BUY: {
                    synchronized (this) {
                        if (currentRate!= null && exOrder.getStop().compareTo(currentRate) <= 0) {
                            this.proceedStopOrders(exOrder.getId());
                        } else {
                            stopOrdersHolder.addOrder(exOrder);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("cant check stop orders {}", e);
        }
    }


}
