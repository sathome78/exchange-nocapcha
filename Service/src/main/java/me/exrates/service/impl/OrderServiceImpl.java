package me.exrates.service.impl;

import com.google.common.base.CaseFormat;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.valueOf;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private CommissionDao commissionDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CompanyWalletService companyWalletService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MessageSource messageSource;


    @Autowired
    private ReferralService referralService;

    @Transactional
    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale) {
        return orderDao.getOrderStatistic(currencyPair, backDealInterval, locale);
    }

    @Transactional
    @Override
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval interval) {
        logger.info("Begin 'getDataForAreaChart' method");
        return orderDao.getDataForAreaChart(currencyPair, interval);
    }

    @Transactional
    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval) {
        logger.info("Begin 'getDataForCandleChart' method");
        return orderDao.getDataForCandleChart(currencyPair, interval);
    }

    @Transactional
    @Override
    public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(CacheData cacheData, Locale locale) {
        List<ExOrderStatisticsShortByPairsDto> result = orderDao.getOrderStatisticByPairs(locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<ExOrderStatisticsShortByPairsDto>() {{
                add(new ExOrderStatisticsShortByPairsDto(false));
            }};
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int createOrder(OrderCreateDto orderCreateDto) {
        int createdOrderId = 0;
        int outWalletId;
        BigDecimal outAmount;
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            outWalletId = orderCreateDto.getWalletIdCurrencyConvert();
            outAmount = orderCreateDto.getTotalWithComission();
        } else {
            outWalletId = orderCreateDto.getWalletIdCurrencyBase();
            outAmount = orderCreateDto.getAmount();
        }
        if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
            ExOrder exOrder = new ExOrder(orderCreateDto);
            if ((createdOrderId = orderDao.createOrder(exOrder)) > 0) {
                exOrder.setId(createdOrderId);
                WalletTransferStatus result = walletService.walletInnerTransfer(outWalletId, outAmount.negate(), TransactionSourceType.ORDER, exOrder.getId());
                if (result != WalletTransferStatus.SUCCESS) {
                    throw new OrderCreationException(result.toString());
                }
                setStatus(exOrder.getId(), OrderStatus.OPENED);
            }
        } else {
            //this exception will be caught in controller, populated  with message text  and thrown further
            throw new NotEnoughUserWalletMoneyException("");
        }
        return createdOrderId;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData,
                                                       String email, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType,
                                                       Integer offset, Integer limit, Locale locale) {
        List<OrderWideListDto> result = orderDao.getMyOrdersWithState(email, currencyPair, status, operationType, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderWideListDto>() {{
                add(new OrderWideListDto(false));
            }};
        }
        return result;
    }

    @Override
    public OrderCreateDto getMyOrderById(int orderId) {
        return orderDao.getMyOrderById(orderId);
    }

    @Transactional(readOnly = true)
    public ExOrder getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Transactional(propagation = Propagation.NESTED)
    public boolean setStatus(int orderId, OrderStatus status) {
        return orderDao.setStatus(orderId, status);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void acceptOrdersList(int userAcceptorId, List<Integer> ordersList, Locale locale) {
        if (orderDao.lockOrdersListForAcception(ordersList)) {
            for (Integer orderId : ordersList) {
                acceptOrder(userAcceptorId, orderId, locale);
            }
        } else {
            throw new OrderAcceptionException(messageSource.getMessage("order.lockerror", null, locale));
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void acceptOrder(int userAcceptorId, int orderId, Locale locale) {
        try {
            ExOrder exOrder = this.getOrderById(orderId);
            WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletDao.getWalletsForOrderByOrderIdAndBlock(exOrder.getId(), userAcceptorId);
            /**/
            if (walletsForOrderAcceptionDto.getOrderStatusId() != 2) {
                throw new OrderAcceptionException(messageSource.getMessage("order.alreadyacceptederror", null, locale));
            }
            /**/
            int createdWalletId;
            if (exOrder.getOperationType() == OperationType.BUY) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), exOrder.getUserId(), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
                }
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userAcceptorId, new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
                }
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), exOrder.getUserId(), new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
                }
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userAcceptorId, new BigDecimal(0)));
                    if (createdWalletId == 0) {
                        throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
                    }
                    walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
                }
            }
            /**/
            /*calculate convert currency amount for creator - simply take stored amount from order*/
            BigDecimal amountWithComissionForCreator = getAmountWithComissionForCreator(exOrder);
            Commission comissionForCreator = new Commission();
            comissionForCreator.setId(exOrder.getComissionId());
            /*calculate convert currency amount for acceptor - calculate at the current commission rate*/
            OperationType operationTypeForAcceptor = exOrder.getOperationType() == OperationType.BUY ? OperationType.SELL : OperationType.BUY;
            Commission comissionForAcceptor = commissionDao.getCommission(operationTypeForAcceptor);
            BigDecimal comissionRateForAcceptor = comissionForAcceptor.getValue();
            BigDecimal amountComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), comissionRateForAcceptor, ActionType.MULTIPLY_PERCENT);
            BigDecimal amountWithComissionForAcceptor;
            if (exOrder.getOperationType() == OperationType.BUY) {
                amountWithComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.SUBTRACT);
            } else {
                amountWithComissionForAcceptor = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), amountComissionForAcceptor, ActionType.ADD);
            }
            /*determine the IN and OUT amounts for creator and acceptor*/
            BigDecimal creatorForOutAmount = null;
            BigDecimal creatorForInAmount = null;
            BigDecimal acceptorForOutAmount = null;
            BigDecimal acceptorForInAmount = null;
            BigDecimal commissionForCreatorOutWallet = null;
            BigDecimal commissionForCreatorInWallet = null;
            BigDecimal commissionForAcceptorOutWallet = null;
            BigDecimal commissionForAcceptorInWallet = null;
            if (exOrder.getOperationType() == OperationType.BUY) {
                commissionForCreatorOutWallet = exOrder.getCommissionFixedAmount();
                commissionForCreatorInWallet = BigDecimal.ZERO;
                commissionForAcceptorOutWallet = BigDecimal.ZERO;
                commissionForAcceptorInWallet = amountComissionForAcceptor;
                /**/
                creatorForOutAmount = amountWithComissionForCreator;
                creatorForInAmount = exOrder.getAmountBase();
                acceptorForOutAmount = exOrder.getAmountBase();
                acceptorForInAmount = amountWithComissionForAcceptor;
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                commissionForCreatorOutWallet = BigDecimal.ZERO;
                commissionForCreatorInWallet = exOrder.getCommissionFixedAmount();
                commissionForAcceptorOutWallet = amountComissionForAcceptor;
                commissionForAcceptorInWallet = BigDecimal.ZERO;
                /**/
                creatorForOutAmount = exOrder.getAmountBase();
                creatorForInAmount = amountWithComissionForCreator;
                acceptorForOutAmount = amountWithComissionForAcceptor;
                acceptorForInAmount = exOrder.getAmountBase();
            }
            WalletOperationData walletOperationData;
            WalletTransferStatus walletTransferStatus;
            /**/
            /*for creator OUT*/
            walletOperationData = new WalletOperationData();
            walletDao.walletInnerTransfer(walletsForOrderAcceptionDto.getUserCreatorOutWalletId(),
                    creatorForOutAmount, TransactionSourceType.ORDER, exOrder.getId());
            walletOperationData.setOperationType(OperationType.OUTPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorOutWalletId());
            walletOperationData.setAmount(creatorForOutAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForCreator);
            walletOperationData.setCommissionAmount(commissionForCreatorOutWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                throw new OrderAcceptionException("creator out wallet: " + walletTransferStatus.toString());
            }
            /*for creator IN*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.INPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorInWalletId());
            walletOperationData.setAmount(creatorForInAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForCreator);
            walletOperationData.setCommissionAmount(commissionForCreatorInWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                throw new OrderAcceptionException("creator input wallet: " + walletTransferStatus.toString());
            }
            /*for acceptor OUT*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.OUTPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId());
            walletOperationData.setAmount(acceptorForOutAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForAcceptor);
            walletOperationData.setCommissionAmount(commissionForAcceptorOutWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                throw new OrderAcceptionException("acceptor out wallet: " + walletTransferStatus.toString());
            }
            /*for acceptor IN*/
            walletOperationData = new WalletOperationData();
            walletOperationData.setOperationType(OperationType.INPUT);
            walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserAcceptorInWalletId());
            walletOperationData.setAmount(acceptorForInAmount);
            walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
            walletOperationData.setCommission(comissionForAcceptor);
            walletOperationData.setCommissionAmount(commissionForAcceptorInWallet);
            walletOperationData.setSourceType(TransactionSourceType.ORDER);
            walletOperationData.setSourceId(exOrder.getId());
            walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
            if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                throw new OrderAcceptionException("acceptor input wallet: " + walletTransferStatus.toString());
            }
            /**/
            CompanyWallet companyWallet = new CompanyWallet();
            companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
            companyWallet.setBalance(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvertBalance());
            companyWallet.setCommissionBalance(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvertCommissionBalance());
            companyWalletService.deposit(companyWallet, new BigDecimal(0), exOrder.getCommissionFixedAmount().add(amountComissionForAcceptor));
            /**/
            exOrder.setStatus(OrderStatus.CLOSED);
            exOrder.setDateAcception(LocalDateTime.now());
            exOrder.setUserAcceptorId(userAcceptorId);
            final Currency currency = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId())
                    .getCurrency2();

            /** TODO: 6/7/16 Temporarily disable the referral program
             * referralService.processReferral(exOrder, exOrder.getCommissionFixedAmount(), currency.getId(), exOrder.getUserId()); //Processing referral for Order Creator
             * referralService.processReferral(exOrder, amountComissionForAcceptor, currency.getId(), exOrder.getUserAcceptorId()); //Processing referral for Order Acceptor
             */

            referralService.processReferral(exOrder, exOrder.getCommissionFixedAmount(), currency, exOrder.getUserId()); //Processing referral for Order Creator
            referralService.processReferral(exOrder, amountComissionForAcceptor, currency, exOrder.getUserAcceptorId()); //Processing referral for Order Acceptor

            if (!updateOrder(exOrder)) {
                throw new OrderAcceptionException(messageSource.getMessage("orders.acceptsaveerror", null, locale));
            }
        } catch (Exception e) {
            logger.error("Error while accepting order with id = " + orderId + " exception: " + e.getLocalizedMessage());
            throw e;
        }
    }

    private String getWalletTransferStatusMessage(WalletTransferStatus status, Locale locale) {
        String message = "";
        switch (status) {
            case CAUSED_NEGATIVE_BALANCE:
                message = messageSource.getMessage("transaction.providerror", null, locale);
                break;
            case CORRESPONDING_COMPANY_WALLET_NOT_FOUND:
                message = messageSource.getMessage("", null, locale);
                break;
            case NOT_FOUND:
                message = messageSource.getMessage("", null, locale);

                break;
            case WALLET_UPDATE_ERROR:
                message = messageSource.getMessage("", null, locale);
                break;
            case TRANSACTION_CREATION_ERROR:
                message = messageSource.getMessage("", null, locale);
                break;
            default:

        }
        return message;
    }


    private BigDecimal getAmountWithComissionForCreator(ExOrder exOrder) {
        if (exOrder.getOperationType() == OperationType.SELL) {
            return BigDecimalProcessing.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.SUBTRACT);
        } else {
            return BigDecimalProcessing.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.ADD);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean cancellOrder(ExOrder exOrder, Locale locale) {
        try {
            WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletDao.getWalletsForOrderByOrderIdAndBlock(exOrder.getId(), null);
            if (OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()) != OrderStatus.OPENED) {
                throw new OrderAcceptionException(messageSource.getMessage("order.cannotcancel", null, locale));
            }
            BigDecimal reservedAmountForCancel = null;
            if (exOrder.getOperationType() == OperationType.SELL) {
                reservedAmountForCancel = exOrder.getAmountBase();
            } else if (exOrder.getOperationType() == OperationType.BUY) {
                reservedAmountForCancel = BigDecimalProcessing.doAction(exOrder.getAmountConvert(), exOrder.getCommissionFixedAmount(), ActionType.ADD);
            }
            WalletTransferStatus transferResult = walletDao.walletInnerTransfer(walletsForOrderAcceptionDto.getUserCreatorOutWalletId(), reservedAmountForCancel, TransactionSourceType.ORDER, exOrder.getId());
            if (transferResult != WalletTransferStatus.SUCCESS) {
                throw new OrderCancellingException(transferResult.toString());
            }
            return setStatus(exOrder.getId(), OrderStatus.CANCELLED);
        } catch (Exception e) {
            logger.error("Error while cancelling order " + exOrder.getId() + " , " + e.getLocalizedMessage());
            throw e;
        }
    }

    private String getStatusString(OrderStatus status, Locale ru) {
        String statusString = null;
        switch (status) {
            case INPROCESS:
                statusString = messageSource.getMessage("orderstatus.inprocess", null, ru);
                break;
            case OPENED:
                statusString = messageSource.getMessage("orderstatus.opened", null, ru);
                break;
            case CLOSED:
                statusString = messageSource.getMessage("orderstatus.closed", null, ru);
                break;
        }
        return statusString;
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public boolean updateOrder(ExOrder exOrder) {
        return orderDao.updateOrder(exOrder);
    }

    @Transactional
    @Override
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval) {
        return orderDao.getCoinmarketData(currencyPairName);
    }

    @Transactional
    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        return orderDao.getOrderInfo(orderId, locale);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer deleteOrderByAdmin(int orderId) {
        Object result = orderDao.deleteOrderByAdmin(orderId);
        if (result instanceof OrderDeleteStatus) {
            if ((OrderDeleteStatus) result == OrderDeleteStatus.NOT_FOUND) {
                return 0;
            }
            throw new OrderDeletingException(((OrderDeleteStatus) result).toString());
        }
        return (Integer) result;
    }

    @Transactional
    @Override
    public Integer searchOrderByAdmin(Integer currencyPair, String orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume) {
        Integer ot = OperationType.valueOf(orderType).getType();
        return orderDao.searchOrderByAdmin(currencyPair, ot, orderDate, orderRate, orderVolume);
    }

    @Transactional
    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(CacheData cacheData,
                                                                   String email,
                                                                   BackDealInterval backDealInterval,
                                                                   Integer limit, CurrencyPair currencyPair, Locale locale) {
        List<OrderAcceptedHistoryDto> result = orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderAcceptedHistoryDto>() {{
                add(new OrderAcceptedHistoryDto(false));
            }};
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderCommissionsDto getCommissionForOrder() {
        return orderDao.getCommissionForOrder();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderListDto> getAllBuyOrders(CacheData cacheData,
                                              CurrencyPair currencyPair, String email, Locale locale) {
        List<OrderListDto> result = orderDao.getOrdersBuyForCurrencyPair(currencyPair, email, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderListDto>() {{
                add(new OrderListDto(false));
            }};
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderListDto> getAllSellOrders(CacheData cacheData,
                                               CurrencyPair currencyPair, String email, Locale locale) {
        List<OrderListDto> result = orderDao.getOrdersSellForCurrencyPair(currencyPair, email, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<OrderListDto>() {{
                add(new OrderListDto(false));
            }};
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType) {
        return orderDao.getWalletAndCommission(email, currency, operationType);
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Transactional
    @Override
    public DataTable<List<OrderBasicInfoDto>> findOrders(Integer currencyPair, String orderType, String orderDateFrom, String orderDateTo,
                                                         BigDecimal orderRate, BigDecimal orderVolume, String creatorEmail, Locale locale) {
        return findOrders(currencyPair, orderType, orderDateFrom, orderDateTo, orderRate, orderVolume, creatorEmail, locale,
                0, Integer.MAX_VALUE, "id", "ASC");

    }

    @Transactional
    @Override
    public DataTable<List<OrderBasicInfoDto>> findOrders(Integer currencyPair, String orderType, String orderDateFrom, String orderDateTo,
                                                         BigDecimal orderRate, BigDecimal orderVolume, String creatorEmail, Locale locale,
                                                         int offset, int limit, String orderColumnName, String orderDirection) {
        Integer ot = null;
        if (!"ANY".equals(orderType)) {
            ot = OperationType.valueOf(orderType).getType();
        }
        if (currencyPair.intValue() == -1) {
            currencyPair = null;
        }
        PagingData<List<OrderBasicInfoDto>> searchResult = orderDao.searchOrders(currencyPair, ot, orderDateFrom,
                orderDateTo, orderRate, orderVolume, creatorEmail, locale, offset, limit, orderColumnName, orderDirection);
        DataTable<List<OrderBasicInfoDto>> output = new DataTable<>();
        output.setData(searchResult.getData());
        output.setRecordsTotal(searchResult.getTotal());
        output.setRecordsFiltered(searchResult.getFiltered());
        return output;

    }

    @Transactional
    @Override
    public DataTable<List<OrderBasicInfoDto>> searchOrdersByAdmin(Integer currencyPair, String orderType, String orderDateFrom, String orderDateTo,
                                                                  BigDecimal orderRate, BigDecimal orderVolume, String creatorEmail, Locale locale,
                                                                  Map<String, String> params) {
        if (params.containsKey("start") && params.containsKey("length")) {
            String orderColumnKey = "columns[" + params.getOrDefault("order[0][column]", "0") + "][data]";
            String orderColumn = params.getOrDefault(orderColumnKey, "id");
            String orderColumnFormatted = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, orderColumn);
            String orderDirection = params.getOrDefault("order[0][dir]", "asc").toUpperCase();


            return findOrders(currencyPair, orderType, orderDateFrom, orderDateTo, orderRate, orderVolume, creatorEmail, locale,
                    valueOf(params.get("start")), valueOf(params.get("length")), orderColumnFormatted, orderDirection);
        }
        return findOrders(currencyPair, orderType, orderDateFrom, orderDateTo, orderRate, orderVolume, creatorEmail, locale);
    }

}


