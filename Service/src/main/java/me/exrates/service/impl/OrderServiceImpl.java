package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.dto.OrderWideListDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.*;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.TransactionPersistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);
    @Autowired
    OrderDao orderDao;
    @Autowired
    WalletDao walletDao;
    @Autowired
    CommissionDao commissionDao;
    @Autowired
    TransactionDao transactionDao;
    @Autowired
    UserService userService;
    @Autowired
    WalletService walletService;
    @Autowired
    CompanyWalletService companyWalletService;
    @Autowired
    CommissionService commissionService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    MessageSource messageSource;

    @Override
    @Transactional
    public int createOrder(int userId, OrderCreateDto orderCreateDto) {
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
            orderCreateDto.setUserId(userId);
            ExOrder exOrder = new ExOrder(orderCreateDto);
            if ((createdOrderId = orderDao.createOrder(exOrder)) > 0) {
                walletService.setWalletRBalance(outWalletId, outAmount);
                walletService.setWalletABalance(outWalletId, outAmount.negate());
                setStatus(createdOrderId, OrderStatus.OPENED);
            }
        } else {
            //this exception will be caught in controller, populated  with message text  and thrown further
            throw new NotEnoughUserWalletMoneyException("");
        }
        return createdOrderId;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, List<OrderWideListDto>> getMyOrders(String email, CurrencyPair currencyPair, Locale locale) {
        int userId = userService.getIdByEmail(email);
        List<ExOrder> orderList = orderDao.getMyOrders(userId, currencyPair);
        /**/
        List<OrderWideListDto> sellOrderList = new ArrayList<>();
        List<OrderWideListDto> buyOrderList = new ArrayList<>();
        for (ExOrder exOrder : orderList) {
            OrderWideListDto orderWideListDto = new OrderWideListDto(exOrder);
            orderWideListDto.setCurrencyPair(currencyService.findCurrencyPairById(exOrder.getCurrencyPairId()));
            orderWideListDto.setStatusString(getStatusString(exOrder.getStatus(), locale));
            /**/
            if (exOrder.getOperationType().equals(OperationType.SELL)) {
                sellOrderList.add(orderWideListDto);
            } else if (exOrder.getOperationType().equals(OperationType.BUY)) {
                buyOrderList.add(orderWideListDto);
            }
        }
        Map<String, List<OrderWideListDto>> orderMap = new HashMap<>();
        orderMap.put("sell", sellOrderList);
        orderMap.put("buy", buyOrderList);
        return orderMap;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, List<OrderWideListDto>> getAllOpenedOrders(Locale locale) {
        List<ExOrder> orderList = orderDao.getAllOpenedOrders();
        /**/
        List<OrderWideListDto> sellOrderList = new ArrayList<>();
        List<OrderWideListDto> buyOrderList = new ArrayList<>();
        for (ExOrder exOrder : orderList) {
            OrderWideListDto orderWideListDto = new OrderWideListDto(exOrder);
            orderWideListDto.setCurrencyPair(currencyService.findCurrencyPairById(exOrder.getCurrencyPairId()));
            orderWideListDto.setStatusString(getStatusString(exOrder.getStatus(), locale));
            if (exOrder.getOperationType().equals(OperationType.SELL)) {
                sellOrderList.add(orderWideListDto);
            } else if (exOrder.getOperationType().equals(OperationType.BUY)) {
                buyOrderList.add(orderWideListDto);
            }
        }
        Map<String, List<OrderWideListDto>> orderMap = new HashMap<>();
        orderMap.put("sell", sellOrderList);
        orderMap.put("buy", buyOrderList);
        return orderMap;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderListDto> getOrdersSell(CurrencyPair currencyPair) {
        return orderDao.getOrdersSell(currencyPair);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderListDto> getOrdersBuy(CurrencyPair currencyPair) {
        return orderDao.getOrdersBuy(currencyPair);
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
    @Override
    public void acceptOrder(int userAcceptorId, int orderId) {
        try {
            ExOrder exOrder = this.getOrderById(orderId);
            WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletDao.getWalletsForOrderByOrderId(exOrder.getId(), userAcceptorId);
            BigDecimal amountWithComissionForCreator = getAmountWithComissionForCreator(exOrder);
            /**/
            OperationType operationTypeForAcceptor = exOrder.getOperationType() == OperationType.BUY ? OperationType.SELL : OperationType.BUY;
            Commission comissionForAcceptor = commissionDao.getCommission(operationTypeForAcceptor);
            BigDecimal comissionRateForAcceptor = comissionForAcceptor.getValue();
            BigDecimal amountComissionForAcceptor = exOrder.getAmountConvert().multiply(comissionRateForAcceptor).divide(new BigDecimal(100));
            BigDecimal amountWithComissionForAcceptor;
            if (exOrder.getOperationType() == OperationType.BUY) {
                amountWithComissionForAcceptor = exOrder.getAmountConvert().add(amountComissionForAcceptor.negate());
            } else {
                amountWithComissionForAcceptor = exOrder.getAmountConvert().add(amountComissionForAcceptor);
            }
            /**/
            if (exOrder.getOperationType() == OperationType.BUY) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), exOrder.getUserId(), new BigDecimal(0)));
                }
                walletService.setWalletABalance(walletsForOrderAcceptionDto.getUserCreatorInWalletId(), exOrder.getAmountBase());
                if (!walletService.setWalletRBalance(walletsForOrderAcceptionDto.getUserCreatorOutWalletId(), amountWithComissionForCreator.negate())) {
                    throw new NotEnoughUserWalletMoneyException("not enough reserved money for creator when accept the order " + orderId);
                }
                /**/
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userAcceptorId, new BigDecimal(0)));
                }
                walletService.setWalletABalance(walletsForOrderAcceptionDto.getUserAcceptorInWalletId(), amountWithComissionForAcceptor);
                if (!walletService.setWalletABalance(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId(), exOrder.getAmountBase().negate())) {
                    throw new NotEnoughUserWalletMoneyException("not enough available money for acceptor when accept the order " + orderId);
                }
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
                    walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), exOrder.getUserId(), new BigDecimal(0)));
                }
                walletService.setWalletABalance(walletsForOrderAcceptionDto.getUserCreatorInWalletId(), amountWithComissionForCreator);
                if (!walletService.setWalletRBalance(walletsForOrderAcceptionDto.getUserCreatorOutWalletId(), exOrder.getAmountBase().negate())) {
                    throw new NotEnoughUserWalletMoneyException("not enough reserved money for creator when accept the order " + orderId);
                }
                /**/
                if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
                    walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userAcceptorId, new BigDecimal(0)));
                }
                walletService.setWalletABalance(walletsForOrderAcceptionDto.getUserAcceptorInWalletId(), exOrder.getAmountBase());
                if (!walletService.setWalletABalance(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId(), amountWithComissionForAcceptor.negate())) {
                    throw new NotEnoughUserWalletMoneyException("not enough available money for acceptor when accept the order " + orderId);
                }
            }
            /**/
            CompanyWallet companyWallet = companyWalletService.findByWalletId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
            companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
            companyWalletService.deposit(companyWallet, new BigDecimal(0), exOrder.getCommissionFixedAmount().add(amountComissionForAcceptor));
            /**/
            Wallet wallet = new Wallet();
            Currency currency = new Currency();
            Commission commission = new Commission();
            Transaction transaction = new Transaction();
            if (exOrder.getOperationType() == OperationType.BUY) {
                /*for creator IN*/
                transaction.setOperationType(OperationType.INPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserCreatorInWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyBase());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(exOrder.getAmountBase());
                transaction.setCommissionAmount(new BigDecimal(0));
                commission.setId(exOrder.getComissionId());
                transaction.setCommission(commission);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyBase());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
                /*for creator OUT*/
                transaction.setOperationType(OperationType.OUTPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserCreatorOutWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(amountWithComissionForCreator);
                transaction.setCommissionAmount(exOrder.getCommissionFixedAmount());
                commission.setId(exOrder.getComissionId());
                transaction.setCommission(commission);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyConvert());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
                /*for acceptor IN*/
                transaction.setOperationType(OperationType.INPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserAcceptorInWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(amountWithComissionForAcceptor);
                transaction.setCommissionAmount(amountComissionForAcceptor);
                transaction.setCommission(comissionForAcceptor);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyConvert());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
                /*for acceptor OUT*/
                transaction.setOperationType(OperationType.OUTPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyBase());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(exOrder.getAmountBase());
                transaction.setCommissionAmount(new BigDecimal(0));
                transaction.setCommission(comissionForAcceptor);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyBase());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
            }
            if (exOrder.getOperationType() == OperationType.SELL) {
                /*for creator IN*/
                transaction.setOperationType(OperationType.INPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserCreatorInWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(amountWithComissionForCreator);
                transaction.setCommissionAmount(exOrder.getCommissionFixedAmount());
                commission.setId(exOrder.getComissionId());
                transaction.setCommission(commission);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyConvert());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
                /*for creator OUT*/
                transaction.setOperationType(OperationType.OUTPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserCreatorOutWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyBase());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(exOrder.getAmountBase());
                transaction.setCommissionAmount(new BigDecimal(0));
                commission.setId(exOrder.getComissionId());
                transaction.setCommission(commission);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyBase());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
                /*for acceptor IN*/
                transaction.setOperationType(OperationType.INPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserAcceptorInWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyBase());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(exOrder.getAmountBase());
                transaction.setCommissionAmount(new BigDecimal(0));
                transaction.setCommission(comissionForAcceptor);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyBase());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
                /*for acceptor OUT*/
                transaction.setOperationType(OperationType.OUTPUT);
                wallet.setId(walletsForOrderAcceptionDto.getUserAcceptorOutWalletId());
                transaction.setUserWallet(wallet);
                companyWallet.setId(walletsForOrderAcceptionDto.getCompanyWalletCurrencyConvert());
                transaction.setCompanyWallet(companyWallet);
                transaction.setAmount(amountWithComissionForAcceptor);
                transaction.setCommissionAmount(amountComissionForAcceptor);
                transaction.setCommission(comissionForAcceptor);
                currency.setId(walletsForOrderAcceptionDto.getCurrencyConvert());
                transaction.setCurrency(currency);
                transaction.setOrder(exOrder);
                transaction.setProvided(true);
                transaction = transactionDao.create(transaction);
                if (transaction == null) {
                    throw new TransactionPersistException("Failed to provide transaction ");
                }
            }
            /**/
            exOrder.setStatus(OrderStatus.CLOSED);
            exOrder.setDateAcception(LocalDateTime.now());
            exOrder.setUserAcceptorId(userAcceptorId);
            updateOrder(exOrder);
        } catch (Exception e) {
            logger.error("Error while accepting order with id = " + orderId + " exception: " + e.getLocalizedMessage());
            throw e;
        }
    }

    private BigDecimal getAmountWithComissionForCreator(ExOrder exOrder) {
        if (exOrder.getOperationType() == OperationType.SELL) {
            return exOrder.getAmountConvert().add(exOrder.getCommissionFixedAmount().negate());
        } else {
            return exOrder.getAmountConvert().add(exOrder.getCommissionFixedAmount());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public boolean cancellOrder(ExOrder exOrder) {
        boolean result = false;
        try {
            if (exOrder.getStatus() == OrderStatus.OPENED) {
                CurrencyPair orderCurrencyPair = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId());
                if (exOrder.getOperationType() == OperationType.SELL) {
                    Wallet walletForCancelReserv = walletDao.findByUserAndCurrency(exOrder.getUserId(), orderCurrencyPair.getCurrency1().getId());
                    walletService.setWalletABalance(walletForCancelReserv.getId(), exOrder.getAmountBase());
                    walletService.setWalletRBalance(walletForCancelReserv.getId(), exOrder.getAmountBase().negate());
                } else {
                    Wallet walletForCancelReserv = walletDao.findByUserAndCurrency(exOrder.getUserId(), orderCurrencyPair.getCurrency2().getId());
                    walletService.setWalletABalance(walletForCancelReserv.getId(), exOrder.getAmountConvert().add(exOrder.getCommissionFixedAmount()));
                    walletService.setWalletRBalance(walletForCancelReserv.getId(), exOrder.getAmountConvert().add(exOrder.getCommissionFixedAmount()).negate());
                }
                result = setStatus(exOrder.getId(), OrderStatus.CANCELLED);
            }
        } catch (Exception e) {
            logger.error("Error while cancelling order " + exOrder.getId() + " , " + e.getLocalizedMessage());
            throw e;
        }
        return result;
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

}
