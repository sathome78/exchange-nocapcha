package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
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
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.util.Cache;
import me.exrates.service.vo.ProfileData;
import org.apache.axis.utils.StringUtils;
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
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

  private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);
  private static final Logger profileLog = LogManager.getLogger("profile");

  private final BigDecimal MAX_ORDER_VALUE = new BigDecimal(10000);
  private final BigDecimal MIN_ORDER_VALUE = new BigDecimal(0.000000001);

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

  @Autowired
  NotificationService notificationService;

  @Autowired
  ServiceCacheableProxy serviceCacheableProxy;


  @Transactional
  @Override
  public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale) {
    ExOrderStatisticsDto result = serviceCacheableProxy.getOrderStatistic(currencyPair, backDealInterval);
    result = new ExOrderStatisticsDto(result);
    result.setFirstOrderAmountBase(BigDecimalProcessing.formatLocale(result.getFirstOrderAmountBase(),locale, true));
    result.setFirstOrderRate(BigDecimalProcessing.formatLocale(result.getFirstOrderRate(), locale, true));
    result.setLastOrderAmountBase(BigDecimalProcessing.formatLocale(result.getLastOrderAmountBase(), locale, true));
    result.setLastOrderRate(BigDecimalProcessing.formatLocale(result.getLastOrderRate(), locale, true));
    result.setMinRate(BigDecimalProcessing.formatLocale(result.getMinRate(), locale, true));
    result.setMaxRate(BigDecimalProcessing.formatLocale(result.getMaxRate(), locale, true));
    result.setSumBase(BigDecimalProcessing.formatLocale(result.getSumBase(), locale, true));
    result.setSumConvert(BigDecimalProcessing.formatLocale(result.getSumConvert(), locale, true));
    return result;
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
    return serviceCacheableProxy.getDataForCandleChart(currencyPair, interval);
  }
  
  @Override
  @Transactional
  public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval interval, LocalDateTime startTime) {
    LocalDateTime endTime = startTime.plus((long) interval.intervalValue, interval.intervalType.getCorrespondingTimeUnit());
    return orderDao.getDataForCandleChart(currencyPair, interval, endTime);
  }

  @Transactional
  @Override
  public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairs(CacheData cacheData, Locale locale) {
    Boolean evictEhCache = cacheData.getForceUpdate() && false;
    List<ExOrderStatisticsShortByPairsDto> result = serviceCacheableProxy.getOrdersStatisticByPairs(evictEhCache);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<ExOrderStatisticsShortByPairsDto>() {{
        add(new ExOrderStatisticsShortByPairsDto(false));
      }};
    } else {
      result = result.stream()
          .map(ExOrderStatisticsShortByPairsDto::new)
          .collect(Collectors.toList());
      result.forEach(e -> {
            BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
            BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
            e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
            e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
            BigDecimal percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
            e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
          }
      );
    }
    return result;
  }

  @Transactional
  @Override
  public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairsSessionless(Locale locale) {
    List<ExOrderStatisticsShortByPairsDto> result = orderDao.getOrderStatisticByPairs();
    result.forEach(e -> {
          BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
          BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
          e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
          e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
          BigDecimal percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
          e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
        }
    );
    return result;
  }

  @Override
  public OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate) {
    Currency spendCurrency = null;
    if (orderType == OperationType.SELL) {
      spendCurrency = activeCurrencyPair.getCurrency1();
    } else if (orderType == OperationType.BUY) {
      spendCurrency = activeCurrencyPair.getCurrency2();
    }
    WalletsAndCommissionsForOrderCreationDto walletsAndCommissions = getWalletAndCommission(userEmail, spendCurrency, orderType);
        /**/
    OrderCreateDto orderCreateDto = new OrderCreateDto();
    orderCreateDto.setOperationType(orderType);
    orderCreateDto.setCurrencyPair(activeCurrencyPair);
    orderCreateDto.setAmount(amount);
    orderCreateDto.setExchangeRate(rate);
    orderCreateDto.setUserId(walletsAndCommissions.getUserId());
    orderCreateDto.setCurrencyPair(activeCurrencyPair);
    if (orderType == OperationType.SELL) {
      orderCreateDto.setWalletIdCurrencyBase(walletsAndCommissions.getSpendWalletId());
      orderCreateDto.setCurrencyBaseBalance(walletsAndCommissions.getSpendWalletActiveBalance());
      orderCreateDto.setComissionForSellId(walletsAndCommissions.getCommissionId());
      orderCreateDto.setComissionForSellRate(walletsAndCommissions.getCommissionValue());
    } else if (orderType == OperationType.BUY) {
      orderCreateDto.setWalletIdCurrencyConvert(walletsAndCommissions.getSpendWalletId());
      orderCreateDto.setCurrencyConvertBalance(walletsAndCommissions.getSpendWalletActiveBalance());
      orderCreateDto.setComissionForBuyId(walletsAndCommissions.getCommissionId());
      orderCreateDto.setComissionForBuyRate(walletsAndCommissions.getCommissionValue());
    }
        /**/
    orderCreateDto.calculateAmounts();
    return orderCreateDto;
  }

  @Override
  public Map<String, Object> validateOrder(OrderCreateDto orderCreateDto) {
    Map<String, Object> errors = new HashMap<>();
    if (orderCreateDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      errors.put("amount_" + errors.size(), "order.fillfield");
    }
    if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
      errors.put("exrate_" + errors.size(), "order.fillfield");
    }
    if (orderCreateDto.getAmount() != null) {
      if (orderCreateDto.getAmount().compareTo(MAX_ORDER_VALUE) == 1) {
        errors.put("amount_" + errors.size(), "order.maxvalue");
        errors.put("amount_" + errors.size(), "order.valuerange");
      }
      if (orderCreateDto.getAmount().compareTo(MIN_ORDER_VALUE) == -1) {
        errors.put("amount_" + errors.size(), "order.minvalue");
        errors.put("amount_" + errors.size(), "order.valuerange");
      }
    }
    if (orderCreateDto.getExchangeRate() != null) {
      if (orderCreateDto.getExchangeRate().compareTo(new BigDecimal(0)) < 1) {
        errors.put("exrate_" + errors.size(), "order.minrate");
      }
    }
    if ((orderCreateDto.getAmount() != null) && (orderCreateDto.getExchangeRate() != null)) {
      boolean ifEnoughMoney = orderCreateDto.getSpentWalletBalance().compareTo(BigDecimal.ZERO) > 0 && orderCreateDto.getSpentAmount().compareTo(orderCreateDto.getSpentWalletBalance()) <= 0;
      if (!ifEnoughMoney) {
        errors.put("balance_" + errors.size(), "validation.orderNotEnoughMoney");
      }
    }
    return errors;
  }

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public int createOrder(OrderCreateDto orderCreateDto) {
    ProfileData profileData = new ProfileData(200);
    try {
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
        profileData.setTime1();
        ExOrder exOrder = new ExOrder(orderCreateDto);
        if ((createdOrderId = orderDao.createOrder(exOrder)) > 0) {
          profileData.setTime2();
          exOrder.setId(createdOrderId);
          WalletTransferStatus result = walletService.walletInnerTransfer(outWalletId, outAmount.negate(), TransactionSourceType.ORDER, exOrder.getId());
          profileData.setTime3();
          if (result != WalletTransferStatus.SUCCESS) {
            throw new OrderCreationException(result.toString());
          }
          setStatus(exOrder.getId(), OrderStatus.OPENED);
          profileData.setTime4();
        }
      } else {
        //this exception will be caught in controller, populated  with message text  and thrown further
        throw new NotEnoughUserWalletMoneyException("");
      }
      return createdOrderId;
    } finally {
      if (profileData.isExceeded()){
        profileLog.warn("slow creation order: "+orderCreateDto+" profile: "+profileData);
      }
    }
  }

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public Optional<String> autoAccept(OrderCreateDto orderCreateDto, Locale locale) {
    Optional<OrderCreationResultDto> autoAcceptResult = autoAcceptOrders(orderCreateDto, locale);
    if (!autoAcceptResult.isPresent()) {
      return Optional.empty();
    }
    OrderCreationResultDto orderCreationResultDto = autoAcceptResult.get();
    StringBuilder successMessage = new StringBuilder("{\"result\":\"");
    if (orderCreationResultDto.getAutoAcceptedQuantity() != null && orderCreationResultDto.getAutoAcceptedQuantity() > 0) {
      successMessage.append(messageSource.getMessage("order.acceptsuccess",
          new Integer[]{orderCreationResultDto.getAutoAcceptedQuantity()}, locale)).append("; ");
    }
    if (orderCreationResultDto.getPartiallyAcceptedAmount() != null) {
      successMessage.append(messageSource.getMessage("orders.partialAccept.success", new Object[]{orderCreationResultDto.getPartiallyAcceptedAmount(),
          orderCreationResultDto.getPartiallyAcceptedOrderFullAmount(), orderCreateDto.getCurrencyPair().getCurrency1().getName()}, locale));
    }
    if (orderCreationResultDto.getCreatedOrderId() != null) {
      successMessage.append(messageSource.getMessage("createdorder.text", null, locale));
    }
    successMessage.append("\"}");
    return Optional.of(successMessage.toString());

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Optional<OrderCreationResultDto> autoAcceptOrders(OrderCreateDto orderCreateDto, Locale locale) {
    ProfileData profileData = new ProfileData(200);
    try {
      List<ExOrder> acceptableOrders = orderDao.selectTopOrders(orderCreateDto.getCurrencyPair().getId(), orderCreateDto.getExchangeRate(),
          OperationType.getOpposite(orderCreateDto.getOperationType()));
      profileData.setTime1();
      logger.debug("acceptableOrders - " + OperationType.getOpposite(orderCreateDto.getOperationType()) + " : " + acceptableOrders);
      if (acceptableOrders.isEmpty()) {
        return Optional.empty();
      }
      BigDecimal cumulativeSum = BigDecimal.ZERO;
      List<ExOrder> ordersForAccept = new ArrayList<>();
      ExOrder orderForPartialAccept = null;
      for (ExOrder order : acceptableOrders) {
        cumulativeSum = cumulativeSum.add(order.getAmountBase());
        if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0) {
          ordersForAccept.add(order);
        } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) == 0) {
          ordersForAccept.add(order);
          break;
        } else {
          orderForPartialAccept = order;
          break;
        }
      }
      OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();

      if (ordersForAccept.size() > 0) {
        acceptOrdersList(orderCreateDto.getUserId(), ordersForAccept.stream().map(ExOrder::getId).collect(Collectors.toList()), locale);
        orderCreationResultDto.setAutoAcceptedQuantity(ordersForAccept.size());
      }
      if (orderForPartialAccept != null) {
        BigDecimal partialAcceptResult = acceptPartially(orderCreateDto, orderForPartialAccept, cumulativeSum, locale);
        orderCreationResultDto.setPartiallyAcceptedAmount(partialAcceptResult);
        orderCreationResultDto.setPartiallyAcceptedOrderFullAmount(orderForPartialAccept.getAmountBase());
      } else if (orderCreateDto.getAmount().compareTo(cumulativeSum) > 0) {
        User user = userService.getUserById(orderCreateDto.getUserId());
        profileData.setTime2();
        OrderCreateDto remainderNew = prepareNewOrder(
            orderCreateDto.getCurrencyPair(),
            orderCreateDto.getOperationType(),
            user.getEmail(),
            orderCreateDto.getAmount().subtract(cumulativeSum),
            orderCreateDto.getExchangeRate());
        profileData.setTime3();
        Integer createdOrderId = createOrder(remainderNew);
        profileData.setTime4();
        orderCreationResultDto.setCreatedOrderId(createdOrderId);
      }
      return Optional.of(orderCreationResultDto);
    } finally {
      if (profileData.isExceeded()) {
        profileLog.warn("slow creation order: " + orderCreateDto + " profile: " + profileData);
      }
    }
  }


  private BigDecimal acceptPartially(OrderCreateDto newOrder, ExOrder orderForPartialAccept, BigDecimal cumulativeSum, Locale locale) {
    deleteOrderForPartialAccept(orderForPartialAccept.getId());
    BigDecimal amountForPartialAccept = newOrder.getAmount().subtract(cumulativeSum.subtract(orderForPartialAccept.getAmountBase()));
    OrderCreateDto accepted = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
        userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), amountForPartialAccept,
        orderForPartialAccept.getExRate());
    OrderCreateDto remainder = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
        userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), orderForPartialAccept.getAmountBase().subtract(amountForPartialAccept),
        orderForPartialAccept.getExRate());
    int acceptedId = createOrder(accepted);
    createOrder(remainder);
    acceptOrder(newOrder.getUserId(), acceptedId, locale, false);
    notificationService.createLocalizedNotification(orderForPartialAccept.getUserId(), NotificationEvent.ORDER,
        "orders.partialAccept.title", "orders.partialAccept.yourOrder",
        new Object[]{orderForPartialAccept.getId(), amountForPartialAccept,
            orderForPartialAccept.getAmountBase(), newOrder.getCurrencyPair().getCurrency1().getName()});
    return amountForPartialAccept;
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderWideListDto> getMyOrdersWithState(CacheData cacheData,
                                                     String email, CurrencyPair currencyPair, OrderStatus status,
                                                     OperationType operationType,
                                                     String scope, Integer offset, Integer limit, Locale locale) {
    List<OrderWideListDto> result = orderDao.getMyOrdersWithState(email, currencyPair, status, operationType, scope, offset, limit, locale);
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
    acceptOrder(userAcceptorId, orderId, locale, true);

  }

  private void acceptOrder(int userAcceptorId, int orderId, Locale locale, boolean sendNotification) {
    try {
      ExOrder exOrder = this.getOrderById(orderId);
      WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletDao.getWalletsForOrderByOrderIdAndBlock(exOrder.getId(), userAcceptorId);
            /**/
      if (walletsForOrderAcceptionDto.getOrderStatusId() != 2) {
        throw new AlreadyAcceptedOrderException(messageSource.getMessage("order.alreadyacceptederror", null, locale));
      }
            /**/
      int createdWalletId;
      if (exOrder.getOperationType() == OperationType.BUY) {
        if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
          createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userService.getUserById(exOrder.getUserId()), new BigDecimal(0)));
          if (createdWalletId == 0) {
            throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
          }
          walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
        }
        if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
          createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userService.getUserById(userAcceptorId), new BigDecimal(0)));
          if (createdWalletId == 0) {
            throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{userAcceptorId}, locale));
          }
          walletsForOrderAcceptionDto.setUserAcceptorInWalletId(createdWalletId);
        }
      }
      if (exOrder.getOperationType() == OperationType.SELL) {
        if (walletsForOrderAcceptionDto.getUserCreatorInWalletId() == 0) {
          createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyConvert(), userService.getUserById(exOrder.getUserId()), new BigDecimal(0)));
          if (createdWalletId == 0) {
            throw new WalletCreationException(messageSource.getMessage("order.createwalleterror", new Object[]{exOrder.getUserId()}, locale));
          }
          walletsForOrderAcceptionDto.setUserCreatorInWalletId(createdWalletId);
        }
        if (walletsForOrderAcceptionDto.getUserAcceptorInWalletId() == 0) {
          createdWalletId = walletService.createNewWallet(new Wallet(walletsForOrderAcceptionDto.getCurrencyBase(), userService.getUserById(userAcceptorId), new BigDecimal(0)));
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
      Commission comissionForAcceptor = commissionDao.getCommission(operationTypeForAcceptor, userService.getCurrentUserRole());
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
      String exceptionMessage = "";
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
        exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "order.notenoughreservedmoneyforcreator", locale);
        if (walletTransferStatus == WalletTransferStatus.CAUSED_NEGATIVE_BALANCE) {
          throw new InsufficientCostsForAcceptionException(exceptionMessage);
        }
        throw new OrderAcceptionException(exceptionMessage);
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
        exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "order.notenoughmoneyforacceptor", locale);
        if (walletTransferStatus == WalletTransferStatus.CAUSED_NEGATIVE_BALANCE) {
          throw new InsufficientCostsForAcceptionException(exceptionMessage);
        }
        throw new OrderAcceptionException(exceptionMessage);
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
        exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "orders.acceptsaveerror", locale);
        throw new OrderAcceptionException(exceptionMessage);
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
        exceptionMessage = getWalletTransferExceptionMessage(walletTransferStatus, "orders.acceptsaveerror", locale);
        throw new OrderAcceptionException(exceptionMessage);
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
      if (sendNotification) {
        notificationService.createLocalizedNotification(exOrder.getUserId(), NotificationEvent.ORDER, "acceptordersuccess.title",
            "acceptorder.message", new Object[]{exOrder.getId()});
      }


    } catch (Exception e) {
      logger.error("Error while accepting order with id = " + orderId + " exception: " + e.getLocalizedMessage());
      throw e;
    }
  }

  private String getWalletTransferExceptionMessage(WalletTransferStatus status, String negativeBalanceMessageCode, Locale locale) {
    String message = "";
    switch (status) {
      case CAUSED_NEGATIVE_BALANCE:
        message = messageSource.getMessage(negativeBalanceMessageCode, null, locale);
        break;
      case CORRESPONDING_COMPANY_WALLET_NOT_FOUND:
        message = messageSource.getMessage("orders.companyWalletNotFound", null, locale);
        break;
      case WALLET_NOT_FOUND:
        message = messageSource.getMessage("orders.walletNotFound", null, locale);
        break;
      case WALLET_UPDATE_ERROR:
        message = messageSource.getMessage("orders.walletUpdateError", null, locale);
        break;
      case TRANSACTION_CREATION_ERROR:
        message = messageSource.getMessage("transaction.createerror", null, locale);
        break;
      default:
        message = messageSource.getMessage("orders.acceptsaveerror", null, locale);

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
    final List<CoinmarketApiDto> result = orderDao.getCoinmarketData(currencyPairName);
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
    result.addAll(currencyPairList.stream()
        .filter(e -> (StringUtils.isEmpty(currencyPairName) || e.getName().equals(currencyPairName))
            && result.stream().noneMatch(r -> r.getCurrency_pair_name().equals(e.getName())))
        .map(CoinmarketApiDto::new)
        .collect(Collectors.toList()));
    return result;
  }

  @Override
  public List<CoinmarketApiDto> getCoinmarketDataForActivePairs(String currencyPairName, BackDealInterval backDealInterval) {
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
    OrderCreateDto order = orderDao.getMyOrderById(orderId);
    Object result = orderDao.deleteOrderByAdmin(orderId);
    if (result instanceof OrderDeleteStatus) {
      if ((OrderDeleteStatus) result == OrderDeleteStatus.NOT_FOUND) {
        return 0;
      }
      throw new OrderDeletingException(((OrderDeleteStatus) result).toString());
    }
    notificationService.notifyUser(order.getUserId(), NotificationEvent.ORDER,
        "deleteOrder.notificationTitle", "deleteOrder.notificationMessage", new Object[]{order.getOrderId()});
    return (Integer) result;
  }

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public Integer deleteOrderForPartialAccept(int orderId) {
    Object result = orderDao.deleteOrderForPartialAccept(orderId);
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
    Boolean evictEhCache = cacheData.getForceUpdate() && false;
    List<OrderAcceptedHistoryDto> result = serviceCacheableProxy.getOrderAcceptedForPeriod(
        email,
        backDealInterval,
        limit,
        currencyPair,
        evictEhCache);
    result = new ArrayList<>(result);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<OrderAcceptedHistoryDto>() {{
        add(new OrderAcceptedHistoryDto(false));
      }};
    } else {
      result = result.stream()
          .map(OrderAcceptedHistoryDto::new)
          .collect(Collectors.toList());
      result.forEach(e -> {
        e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      });
    }
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public OrderCommissionsDto getCommissionForOrder() {
    return orderDao.getCommissionForOrder(userService.getCurrentUserRole());
  }

  @Transactional(readOnly = true)
  @Override
  public CommissionsDto getAllCommissions() {
    UserRole userRole = userService.getCurrentUserRole();
    return orderDao.getAllCommissions(userRole);
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllBuyOrders(CacheData cacheData,
                                            CurrencyPair currencyPair, Locale locale) {
    Boolean evictEhCache = cacheData.getForceUpdate();
    List<OrderListDto> result = serviceCacheableProxy.getAllBuyOrders(currencyPair, evictEhCache);
    result = new ArrayList<>(result);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<OrderListDto>() {{
        add(new OrderListDto(false));
      }};
    } else {
      result = result.stream()
          .map(OrderListDto::new)
          .collect(Collectors.toList());
      result.forEach(e -> {
        e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
      });
    }
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllSellOrders(CacheData cacheData,
                                             CurrencyPair currencyPair, Locale locale) {
    Boolean evictEhCache = cacheData.getForceUpdate();
    List<OrderListDto> result = serviceCacheableProxy.getAllSellOrders(currencyPair, evictEhCache);
    result = new ArrayList<>(result);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<OrderListDto>() {{
        add(new OrderListDto(false));
      }};
    } else {
      result = result.stream()
          .map(OrderListDto::new)
          .collect(Collectors.toList());
      result.forEach(e -> {
        e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
      });
    }
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                         OperationType operationType) {
    return orderDao.getWalletAndCommission(email, currency, operationType, userService.getCurrentUserRole());
  }

  public void setMessageSource(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }


    @Override
    @Transactional
    public DataTable<List<OrderBasicInfoDto>> searchOrdersByAdmin(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale) {

        PagingData<List<OrderBasicInfoDto>> searchResult = orderDao.searchOrders(adminOrderFilterData, dataTableParams, locale);
        DataTable<List<OrderBasicInfoDto>> output = new DataTable<>();
        output.setData(searchResult.getData());
        output.setRecordsTotal(searchResult.getTotal());
        output.setRecordsFiltered(searchResult.getFiltered());
        return output;
    }

  @Transactional(readOnly = true)
  @Override
  public List<OrderWideListDto> getUsersOrdersWithStateForAdmin(String email, CurrencyPair currencyPair, OrderStatus status,
                                                                OperationType operationType,
                                                                Integer offset, Integer limit, Locale locale) {
    List<OrderWideListDto> result = orderDao.getMyOrdersWithState(email, currencyPair, status, operationType, null, offset, limit, locale);

    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                     OperationType operationType,
                                                     Integer offset, Integer limit, Locale locale) {
    return orderDao.getMyOrdersWithState(email, currencyPair, status, operationType, null, offset, limit, locale);
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                     OperationType operationType,
                                                     Integer offset, Integer limit, Locale locale) {
    return orderDao.getMyOrdersWithState(email, currencyPair, statuses, operationType, null, offset, limit, locale);
  }


  @Transactional
  @Override
  public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email,
                                                                 BackDealInterval backDealInterval,
                                                                 Integer limit, CurrencyPair currencyPair, Locale locale) {
    List<OrderAcceptedHistoryDto> result = orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair);
    result.forEach(e -> {
      e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
      e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
    });
    return result;
  }


  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllBuyOrders(CurrencyPair currencyPair, Locale locale) {
    List<OrderListDto> result = orderDao.getOrdersBuyForCurrencyPair(currencyPair);
    result.forEach(e -> {
      e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
      e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
    });
    return result;
  }


  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllSellOrders(CurrencyPair currencyPair, Locale locale) {
    List<OrderListDto> result = orderDao.getOrdersSellForCurrencyPair(currencyPair);
    result.forEach(e -> {
      e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
      e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
    });
    return result;
  }


}


