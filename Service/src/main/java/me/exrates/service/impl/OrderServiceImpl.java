package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
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
import me.exrates.model.vo.TransactionDescription;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.CancelOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.exception.*;
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.stopOrder.RatesHolder;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.util.Cache;
import me.exrates.service.vo.ProfileData;
import org.apache.axis.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static me.exrates.model.enums.OrderActionEnum.*;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

  private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

  private final BigDecimal MAX_ORDER_VALUE = new BigDecimal(100000);
  private final BigDecimal MIN_ORDER_VALUE = new BigDecimal(0.000000001);

  @Autowired
  private OrderDao orderDao;

  @Autowired
  private CommissionDao commissionDao;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private UserService userService;

  @Autowired
  private WalletService walletService;

  @Autowired
  private CompanyWalletService companyWalletService;

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

  @Autowired
  TransactionDescription transactionDescription;

  @Autowired
  StopOrderService stopOrderService;
  @Autowired
  RatesHolder ratesHolder;
  @Autowired
  private UserRoleService userRoleService;
  @Autowired
  private BotService botService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale) {
    ExOrderStatisticsDto result = serviceCacheableProxy.getOrderStatistic(currencyPair, backDealInterval);
    result = new ExOrderStatisticsDto(result);
    result.setPercentChange(BigDecimalProcessing.formatLocale(BigDecimalProcessing.doAction(
            result.getFirstOrderRate(), result.getLastOrderRate(), ActionType.PERCENT_GROWTH),
            locale, 2));
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
                .collect(toList());
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
   @Transactional(readOnly = true)
   @Override
   public List<ExOrderStatisticsShortByPairsDto> getOrdersStatisticByPairsEx() {
     Locale locale = Locale.ENGLISH;
     List<ExOrderStatisticsShortByPairsDto> result = orderDao.getOrderStatisticByPairs();
     result = result.stream()
             .map(ExOrderStatisticsShortByPairsDto::new)
             .collect(toList());
     result.forEach(e -> {
       BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
       BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
       e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
       e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
       BigDecimal percentChange = null;
       if (predLastRate.equals(BigDecimal.ZERO)) {
         percentChange = BigDecimal.ZERO;
       }  else {
         percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
       }
       e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
     });
     return result;
   }

    @Transactional(readOnly = true)
    @Override
    public List<ExOrderStatisticsShortByPairsDto> getStatForSomeCurrencies(List<Integer> pairsIds) {
      List<ExOrderStatisticsShortByPairsDto> dto = orderDao.getOrderStatisticForSomePairs(pairsIds);
      Locale locale = Locale.ENGLISH;
      dto.forEach(e -> {
        BigDecimal lastRate = new BigDecimal(e.getLastOrderRate());
        BigDecimal predLastRate = e.getPredLastOrderRate() == null ? lastRate : new BigDecimal(e.getPredLastOrderRate());
        e.setLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(lastRate, locale, 12));
        e.setPredLastOrderRate(BigDecimalProcessing.formatLocaleFixedSignificant(predLastRate, locale, 12));
        BigDecimal percentChange = BigDecimalProcessing.doAction(predLastRate, lastRate, ActionType.PERCENT_GROWTH);
        e.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, locale, 2));
      });  return dto;
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
    return prepareNewOrder(activeCurrencyPair, orderType, userEmail, amount, rate, null);
  }
  
  @Override
  public OrderCreateDto prepareNewOrder(CurrencyPair activeCurrencyPair, OperationType orderType, String userEmail, BigDecimal amount, BigDecimal rate, Integer sourceId) {
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
    orderCreateDto.setSourceId(sourceId);
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
  public OrderValidationDto validateOrder(OrderCreateDto orderCreateDto) {
    OrderValidationDto orderValidationDto = new OrderValidationDto();
    Map<String, Object> errors = orderValidationDto.getErrors();
    Map<String, Object[]> errorParams = orderValidationDto.getErrorParams();
    if (orderCreateDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      errors.put("amount_" + errors.size(), "order.fillfield");
    }
    if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
      errors.put("exrate_" + errors.size(), "order.fillfield");
    }

    CurrencyPairLimitDto currencyPairLimit = currencyService.findLimitForRoleByCurrencyPairAndType(orderCreateDto.getCurrencyPair().getId(),
            orderCreateDto.getOperationType());
    if (orderCreateDto.getOrderBaseType() != null && orderCreateDto.getOrderBaseType().equals(OrderBaseType.STOP_LIMIT)) {
      if (orderCreateDto.getStop() == null || orderCreateDto.getStop().compareTo(BigDecimal.ZERO) <= 0) {
        errors.put("stop_" + errors.size(), "order.fillfield");
      } else {
        if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMinRate()) < 0) {
          String key = "stop_" + errors.size();
          errors.put(key, "order.minrate");
          errorParams.put(key, new Object[]{currencyPairLimit.getMinRate()});
        }
        if (orderCreateDto.getStop().compareTo(currencyPairLimit.getMaxRate()) > 0) {
          String key = "stop_" + errors.size();
          errors.put(key, "order.maxrate");
          errorParams.put(key, new Object[]{currencyPairLimit.getMaxRate()});
        }
      }
    }
    if (orderCreateDto.getAmount() != null) {
      if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMaxAmount()) > 0) {
        String key1 = "amount_" + errors.size();
        errors.put(key1, "order.maxvalue");
        errorParams.put(key1, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
        String key2 = "amount_" + errors.size();
        errors.put(key2, "order.valuerange");
        errorParams.put(key2, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
      }
      if (orderCreateDto.getAmount().compareTo(currencyPairLimit.getMinAmount()) < 0) {
        String key1 = "amount_" + errors.size();
        errors.put(key1, "order.minvalue");
        errorParams.put(key1, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false)});
        String key2 = "amount_" + errors.size();
        errors.put(key2, "order.valuerange");
        errorParams.put(key2, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinAmount(), false),
                BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxAmount(), false)});
      }
    }
    if (orderCreateDto.getExchangeRate() != null) {
      if (orderCreateDto.getExchangeRate().compareTo(BigDecimal.ZERO) < 1) {
        errors.put("exrate_" + errors.size(), "order.zerorate");
      }
      if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMinRate()) < 0) {
        String key = "exrate_" + errors.size();
        errors.put(key, "order.minrate");
        errorParams.put(key, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMinRate(), false)});
      }
      if (orderCreateDto.getExchangeRate().compareTo(currencyPairLimit.getMaxRate()) > 0) {
        String key = "exrate_" + errors.size();
        errors.put(key, "order.maxrate");
        errorParams.put(key, new Object[]{BigDecimalProcessing.formatNonePoint(currencyPairLimit.getMaxRate(), false)});
      }
      
    }
    if ((orderCreateDto.getAmount() != null) && (orderCreateDto.getExchangeRate() != null)) {
      boolean ifEnoughMoney = orderCreateDto.getSpentWalletBalance().compareTo(BigDecimal.ZERO) > 0 && orderCreateDto.getSpentAmount().compareTo(orderCreateDto.getSpentWalletBalance()) <= 0;
      if (!ifEnoughMoney) {
        errors.put("balance_" + errors.size(), "validation.orderNotEnoughMoney");
      }
    }
    return orderValidationDto;
  }

  @Override
  public String createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale) {
    Optional<String> autoAcceptResult = this.autoAccept(orderCreateDto, locale);
    if (autoAcceptResult.isPresent()) {
      logger.debug(autoAcceptResult.get());
      return autoAcceptResult.get();
    }
    Integer orderId = this.createOrder(orderCreateDto, CREATE);
    if (orderId <= 0) {
      throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
    }
    return "{\"result\":\"" + messageSource.getMessage("createdorder.text", null, locale) + "\"}";
  }

  @Override
  @Transactional
  public Integer createOrderByStopOrder(OrderCreateDto orderCreateDto, OrderActionEnum action, Locale locale) {
      Optional<OrderCreationResultDto> autoAcceptResult = this.autoAcceptOrders(orderCreateDto, locale);
      if (autoAcceptResult.isPresent()) {
        logger.debug(autoAcceptResult.get());
        return autoAcceptResult.get().getCreatedOrderId();
      }
    Integer orderId = this.createOrder(orderCreateDto, CREATE);
    if (orderId <= 0) {
      throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, locale));
    }
    return orderId;
  }
  
  
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action) {
    ProfileData profileData = new ProfileData(200);
    try {
      String description = transactionDescription.get(null, action);
      int createdOrderId;
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
        OrderBaseType orderBaseType = orderCreateDto.getOrderBaseType();
        if (orderBaseType == null) {
          orderBaseType = OrderBaseType.LIMIT;
          exOrder.setOrderBaseType(OrderBaseType.LIMIT);
        }
        TransactionSourceType sourceType;
        switch (orderBaseType) {
          case STOP_LIMIT: {
            createdOrderId = stopOrderService.createOrder(exOrder);
            sourceType = TransactionSourceType.STOP_ORDER;
            break;
          }
          default: {
            createdOrderId = orderDao.createOrder(exOrder);
            sourceType = TransactionSourceType.ORDER;
          }
        }
        if (createdOrderId > 0) {
          profileData.setTime2();
          exOrder.setId(createdOrderId);
          WalletTransferStatus result = walletService.walletInnerTransfer(
                  outWalletId,
                  outAmount.negate(),
                  sourceType,
                  exOrder.getId(),
                  description);
          profileData.setTime3();
          if (result != WalletTransferStatus.SUCCESS) {
            throw new OrderCreationException(result.toString());
          }
          setStatus(createdOrderId, OrderStatus.OPENED, exOrder.getOrderBaseType());
          profileData.setTime4();
        }
        eventPublisher.publishEvent(new CreateOrderEvent(exOrder));
        return createdOrderId;

      } else {
        //this exception will be caught in controller, populated  with message text  and thrown further
        throw new NotEnoughUserWalletMoneyException("");
      }
    } finally {
      profileData.checkAndLog("slow creation order: "+orderCreateDto+" profile: "+profileData);
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
      boolean acceptSameRoleOnly = userRoleService.isOrderAcceptionAllowedForUser(orderCreateDto.getUserId());
      List<ExOrder> acceptableOrders = orderDao.selectTopOrders(orderCreateDto.getCurrencyPair().getId(), orderCreateDto.getExchangeRate(),
          OperationType.getOpposite(orderCreateDto.getOperationType()), acceptSameRoleOnly, userService.getUserRoleFromDB(orderCreateDto.getUserId()).getRole());
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
        acceptOrdersList(orderCreateDto.getUserId(), ordersForAccept.stream().map(ExOrder::getId).collect(toList()), locale);
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
        Integer createdOrderId = createOrder(remainderNew, CREATE);
        profileData.setTime4();
        orderCreationResultDto.setCreatedOrderId(createdOrderId);
      }
      return Optional.of(orderCreationResultDto);
    } finally {
      profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
    }
  }


  private BigDecimal acceptPartially(OrderCreateDto newOrder, ExOrder orderForPartialAccept, BigDecimal cumulativeSum, Locale locale) {
    deleteOrderForPartialAccept(orderForPartialAccept.getId());
    BigDecimal amountForPartialAccept = newOrder.getAmount().subtract(cumulativeSum.subtract(orderForPartialAccept.getAmountBase()));
    OrderCreateDto accepted = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
        userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), amountForPartialAccept,
        orderForPartialAccept.getExRate(), orderForPartialAccept.getId());
    OrderCreateDto remainder = prepareNewOrder(newOrder.getCurrencyPair(), orderForPartialAccept.getOperationType(),
        userService.getUserById(orderForPartialAccept.getUserId()).getEmail(), orderForPartialAccept.getAmountBase().subtract(amountForPartialAccept),
        orderForPartialAccept.getExRate(), orderForPartialAccept.getId());
    int acceptedId = createOrder(accepted, CREATE);
    createOrder(remainder, CREATE_SPLIT);
    acceptOrder(newOrder.getUserId(), acceptedId, locale, false);
    notificationService.createLocalizedNotification(orderForPartialAccept.getUserId(), NotificationEvent.ORDER,
        "orders.partialAccept.title", "orders.partialAccept.yourOrder",
        new Object[]{orderForPartialAccept.getId(), amountForPartialAccept.toString(),
            orderForPartialAccept.getAmountBase().toString(), newOrder.getCurrencyPair().getCurrency1().getName()});
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

  @Transactional
  public boolean setStatus(int orderId, OrderStatus status, OrderBaseType orderBaseType) {
    switch (orderBaseType) {
      case STOP_LIMIT: {
        return stopOrderService.setStatus(orderId, status);
      }
      default: {
        return this.setStatus(orderId, status);
      }
    }
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
  private void acceptOrder(int userAcceptorId, int orderId, Locale locale) {
    acceptOrder(userAcceptorId, orderId, locale, true);

  }
  
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void acceptOrderByAdmin(String acceptorEmail, Integer orderId, Locale locale) {
    Integer userId = userService.getIdByEmail(acceptorEmail);
    acceptOrdersList(userId, Collections.singletonList(orderId), locale);
  }


  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void acceptManyOrdersByAdmin(String acceptorEmail, List<Integer> orderIds, Locale locale) {
    Integer userId = userService.getIdByEmail(acceptorEmail);
    acceptOrdersList(userId, orderIds, locale);
  }


  private void acceptOrder(int userAcceptorId, int orderId, Locale locale, boolean sendNotification) {
    try {
      ExOrder exOrder = this.getOrderById(orderId);

      checkAcceptPermissionForUser(userAcceptorId, exOrder.getUserId(), locale);

      WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = walletService.getWalletsForOrderByOrderIdAndBlock(exOrder.getId(), userAcceptorId);
      String descriptionForCreator = transactionDescription.get(OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()), ACCEPTED);
      String descriptionForAcceptor = transactionDescription.get(OrderStatus.convert(walletsForOrderAcceptionDto.getOrderStatusId()), ACCEPT);
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
      Commission comissionForAcceptor = commissionDao.getCommission(operationTypeForAcceptor, userService.getUserRoleFromDB(userAcceptorId));
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
      walletService.walletInnerTransfer(
          walletsForOrderAcceptionDto.getUserCreatorOutWalletId(),
          creatorForOutAmount,
          TransactionSourceType.ORDER,
          exOrder.getId(),
          descriptionForCreator);
      walletOperationData.setOperationType(OperationType.OUTPUT);
      walletOperationData.setWalletId(walletsForOrderAcceptionDto.getUserCreatorOutWalletId());
      walletOperationData.setAmount(creatorForOutAmount);
      walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
      walletOperationData.setCommission(comissionForCreator);
      walletOperationData.setCommissionAmount(commissionForCreatorOutWallet);
      walletOperationData.setSourceType(TransactionSourceType.ORDER);
      walletOperationData.setSourceId(exOrder.getId());
      walletOperationData.setDescription(descriptionForCreator);
      walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
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
      walletOperationData.setDescription(descriptionForAcceptor);
      walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
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
      walletOperationData.setDescription(descriptionForCreator);
      walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
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
      walletOperationData.setDescription(descriptionForAcceptor);
      walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
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
      /*if (sendNotification) {
        notificationService.createLocalizedNotification(exOrder.getUserId(), NotificationEvent.ORDER, "acceptordersuccess.title",
            "acceptorder.message", new Object[]{exOrder.getId()});
      }*/

    /*  stopOrderService.onLimitOrderAccept(exOrder);*//*check stop-orders for process*/
      /*action for refresh orders*/
      eventPublisher.publishEvent(new AcceptOrderEvent(exOrder));
    } catch (Exception e) {
      logger.error("Error while accepting order with id = " + orderId + " exception: " + e.getLocalizedMessage());
      throw e;
    }
  }

  private void checkAcceptPermissionForUser(Integer acceptorId, Integer creatorId, Locale locale) {
    UserRole acceptorRole = userService.getUserRoleFromDB(acceptorId);
    UserRole creatorRole = userService.getUserRoleFromDB(creatorId);

    UserRoleSettings creatorSettings = userRoleService.retrieveSettingsForRole(creatorRole.getRole());
    if (creatorSettings.isBotAcceptionAllowedOnly() && acceptorRole != UserRole.BOT_TRADER) {
      throw new AttemptToAcceptBotOrderException(messageSource.getMessage("orders.acceptsaveerror", null, locale));
    }
    if (userRoleService.isOrderAcceptionAllowedForUser(acceptorId)) {
      if (acceptorRole != creatorRole) {
        throw new OrderAcceptionException(messageSource.getMessage("order.accept.wrongRole", new Object[]{creatorRole.name()}, locale));
      }

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
      WalletsForOrderCancelDto walletsForOrderCancelDto = walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(
          exOrder.getId(),
          exOrder.getOperationType());
      OrderStatus currentStatus = OrderStatus.convert(walletsForOrderCancelDto.getOrderStatusId());
      if (currentStatus != OrderStatus.OPENED) {
        throw new OrderAcceptionException(messageSource.getMessage("order.cannotcancel", null, locale));
      }
      String description = transactionDescription.get(currentStatus, CANCEL);
      WalletTransferStatus transferResult = walletService.walletInnerTransfer(
          walletsForOrderCancelDto.getWalletId(),
          walletsForOrderCancelDto.getReservedAmount(),
          TransactionSourceType.ORDER,
          exOrder.getId(),
          description);
      if (transferResult != WalletTransferStatus.SUCCESS) {
        throw new OrderCancellingException(transferResult.toString());
      }

      boolean result = setStatus(exOrder.getId(), OrderStatus.CANCELLED);
      if (result) {
        eventPublisher.publishEvent(new CancelOrderEvent(exOrder, false));
      }
      return result;
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
        .collect(toList()));
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

  @Transactional
  @Override
  public AdminOrderInfoDto getAdminOrderInfo(int orderId, Locale locale) {
    AdminOrderInfoDto dto = new AdminOrderInfoDto(this.getOrderInfo(orderId, locale));
    setIsOrderAcceptableAndNotifications(dto, locale);
    return dto;
  }

  private void setIsOrderAcceptableAndNotifications(AdminOrderInfoDto dto, Locale locale) {
    UserRole orderCreatorRole = userService.getUserRoleFromDB(dto.getOrderInfo().getOrderCreatorEmail());
    UserRole userRole = userService.getUserRoleFromSecurityContext();
    if (orderCreatorRole.getRole() == UserRole.TRADER.getRole() &&
            userRoleService.getRealUserRoleIdByBusinessRoleList(BusinessUserRoleEnum.ADMIN).contains(userRole.getRole())) {
        dto.setNotification(messageSource.getMessage("admin.orders.accept.warning", null, locale));
        dto.setAcceptable(true);
    } else if (userRole.getRole() == UserRole.TRADER.getRole() && orderCreatorRole.getRole() != UserRole.TRADER.getRole()) {
      dto.setAcceptable(false);
      dto.setNotification(messageSource.getMessage("admin.orders.cantaccept", null, locale));
    } else {
      dto.setAcceptable(true);
    }
  }

  @Override
  @Transactional(rollbackFor = {Exception.class})
  public void deleteManyOrdersByAdmin(List<Integer> orderIds) {
    orderIds.forEach(this::deleteOrderByAdmin);
  }

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public Integer deleteOrderByAdmin(int orderId) {
    OrderCreateDto order = orderDao.getMyOrderById(orderId);
    Object result = deleteOrder(orderId, OrderStatus.DELETED, DELETE);
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
    Object result = deleteOrder(orderId, OrderStatus.SPLIT_CLOSED, DELETE_SPLIT);
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
          .collect(toList());
      result.forEach(e -> {
        e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      });
    }
    return result;
  }

  @Transactional
  @Override
  public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriodEx(String email,
                                                                   BackDealInterval backDealInterval,
                                                                   Integer limit, CurrencyPair currencyPair, Locale locale) {
    List<OrderAcceptedHistoryDto> result = orderDao.getOrderAcceptedForPeriod(email, backDealInterval, limit, currencyPair);
    result = result.stream()
              .map(OrderAcceptedHistoryDto::new)
              .collect(toList());
      result.forEach(e -> {
        e.setRate(BigDecimalProcessing.formatLocale(e.getRate(), locale, true));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      });
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public OrderCommissionsDto getCommissionForOrder() {
    return orderDao.getCommissionForOrder(userService.getUserRoleFromSecurityContext());
  }

  @Transactional(readOnly = true)
  @Override
  public CommissionsDto getAllCommissions() {
    UserRole userRole = userService.getUserRoleFromSecurityContext();
    return orderDao.getAllCommissions(userRole);
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllBuyOrders(CacheData cacheData,
                                            CurrencyPair currencyPair, Locale locale, Boolean orderRoleFilterEnabled) {
    Boolean evictEhCache = cacheData.getForceUpdate();
    UserRole filterRole = orderRoleFilterEnabled ? userService.getUserRoleFromSecurityContext() : null;
    List<OrderListDto> result = aggregateOrders(serviceCacheableProxy.getAllBuyOrders(currencyPair, filterRole, evictEhCache), OperationType.BUY, evictEhCache);
    result = new ArrayList<>(result);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<OrderListDto>() {{
        add(new OrderListDto(false));
      }};
    } else {
      result = result.stream()
          .map(OrderListDto::new).sorted(new Comparator<OrderListDto>() {
                @Override
                public int compare(OrderListDto o1, OrderListDto o2) {
                  return Double.valueOf(o2.getExrate()).compareTo(Double.valueOf(o1.getExrate()));
                }
              })
          .collect(toList());
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
  public List<OrderListDto> getAllBuyOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole) {
    List<OrderListDto> result = aggregateOrders(orderDao.getOrdersBuyForCurrencyPair(currencyPair, userRole), OperationType.BUY, true);
    result = new ArrayList<>(result);
    result = result.stream()
              .map(OrderListDto::new).sorted(new Comparator<OrderListDto>() {
                @Override
                public int compare(OrderListDto o1, OrderListDto o2) {
                  return Double.valueOf(o2.getExrate()).compareTo(Double.valueOf(o1.getExrate()));
                }
              })
              .collect(toList());
      result.forEach(e -> {
        e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
      });
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllSellOrdersEx(CurrencyPair currencyPair, Locale locale, UserRole userRole) {
    List<OrderListDto> result = aggregateOrders(orderDao.getOrdersSellForCurrencyPair(currencyPair, userRole), OperationType.SELL, true);
    result = new ArrayList<>(result);
    result = result.stream()
            .map(OrderListDto::new).sorted(new Comparator<OrderListDto>() {
              @Override
              public int compare(OrderListDto o1, OrderListDto o2) {
                return Double.valueOf(o1.getExrate()).compareTo(Double.valueOf(o2.getExrate()));
              }
            })
            .collect(toList());
    result.forEach(e -> {
      e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
      e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
    });
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public List<OrderListDto> getAllSellOrders(CacheData cacheData,
                                             CurrencyPair currencyPair, Locale locale, Boolean orderRoleFilterEnabled) {
    Boolean evictEhCache = cacheData.getForceUpdate();
    UserRole filterRole = orderRoleFilterEnabled ? userService.getUserRoleFromSecurityContext() : null;
    List<OrderListDto> result = aggregateOrders(serviceCacheableProxy.getAllSellOrders(currencyPair, filterRole, evictEhCache), OperationType.SELL, evictEhCache);
    result = new ArrayList<>(result);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<OrderListDto>() {{
        add(new OrderListDto(false));
      }};
    } else {
      result = result.stream()
          .map(OrderListDto::new).sorted(new Comparator<OrderListDto>() {
                @Override
                public int compare(OrderListDto o1, OrderListDto o2) {
                  return Double.valueOf(o1.getExrate()).compareTo(Double.valueOf(o2.getExrate()));
                }
              })
          .collect(toList());
      result.forEach(e -> {
        e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
        e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
        e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
      });
    }
    return result;
  }

  private List<OrderListDto> aggregateOrders(List<OrderListDto> historyDtos, OperationType operationType, boolean forceUpdate) {
    List<OrderListDto> resultList = new ArrayList<>();
    Map<String, List<OrderListDto>> map =
            historyDtos.stream().collect(Collectors.groupingBy(OrderListDto::getExrate));
    map.forEach((k,v)-> {
      BigDecimal amountBase = new BigDecimal(0);
      BigDecimal amountConverted = new BigDecimal(0);
      StringJoiner ordersIds = new StringJoiner(" ");
      for (OrderListDto order : v) {
        amountBase = amountBase.add(new BigDecimal(order.getAmountBase()));
        amountConverted = amountConverted.add(new BigDecimal(order.getAmountConvert()));
        ordersIds.add(String.valueOf(order.getId()));
      }
      resultList.add(new OrderListDto(ordersIds.toString(), k, amountBase.toString(),
              amountConverted.toString(), operationType, forceUpdate));
    });
    return resultList;
  }

  @Transactional(readOnly = true)
  @Override
  public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                         OperationType operationType) {
    UserRole userRole = null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      userRole = userService.getUserRoleFromDB(email);
    } else {
      userRole = userService.getUserRoleFromSecurityContext();
    }
    return orderDao.getWalletAndCommission(email, currency, operationType, userRole);
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
    List<OrderListDto> result = orderDao.getOrdersBuyForCurrencyPair(currencyPair, null);
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
    List<OrderListDto> result = orderDao.getOrdersSellForCurrencyPair(currencyPair, null);
    result.forEach(e -> {
      e.setExrate(BigDecimalProcessing.formatLocale(e.getExrate(), locale, 2));
      e.setAmountBase(BigDecimalProcessing.formatLocale(e.getAmountBase(), locale, true));
      e.setAmountConvert(BigDecimalProcessing.formatLocale(e.getAmountConvert(), locale, true));
    });
    return result;
  }


  @Transactional
  private Object deleteOrder(int orderId, OrderStatus newOrderStatus, OrderActionEnum action) {
    List<OrderDetailDto> list = walletService.getOrderRelatedDataAndBlock(orderId);
    if (list.isEmpty()) {
      return OrderDeleteStatus.NOT_FOUND;
    }
    int processedRows = 1;
    /**/
    OrderStatus currentOrderStatus = list.get(0).getOrderStatus();
    String description = transactionDescription.get(currentOrderStatus, action);
    /**/
    if (!setStatus(orderId, newOrderStatus)){
      return OrderDeleteStatus.ORDER_UPDATE_ERROR;
    }
     /**/
    for (OrderDetailDto orderDetailDto : list) {
      if (currentOrderStatus == OrderStatus.CLOSED) {
        if (orderDetailDto.getCompanyCommission().compareTo(BigDecimal.ZERO) != 0) {
          Integer companyWalletId = orderDetailDto.getCompanyWalletId();
          if (companyWalletId != 0 && !companyWalletService.substractCommissionBalanceById(companyWalletId, orderDetailDto.getCompanyCommission())) {
            return OrderDeleteStatus.COMPANY_WALLET_UPDATE_ERROR;
          }
        }
        /**/
        WalletOperationData walletOperationData = new WalletOperationData();
        OperationType operationType = null;
        if (orderDetailDto.getTransactionType() == OperationType.OUTPUT) {
          operationType = OperationType.INPUT;
        } else if (orderDetailDto.getTransactionType() == OperationType.INPUT) {
          operationType = OperationType.OUTPUT;
        }
        if (operationType != null) {
          walletOperationData.setOperationType(operationType);
          walletOperationData.setWalletId(orderDetailDto.getUserWalletId());
          walletOperationData.setAmount(orderDetailDto.getTransactionAmount());
          walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
          Commission commission = commissionDao.getDefaultCommission(OperationType.STORNO);
          walletOperationData.setCommission(commission);
          walletOperationData.setCommissionAmount(commission.getValue());
          walletOperationData.setSourceType(TransactionSourceType.ORDER);
          walletOperationData.setSourceId(orderId);
          walletOperationData.setDescription(description);
          WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
          if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
            return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
          }
        }
        log.debug("rows before refs {}", processedRows);
        int processedRefRows = this.unprocessReferralTransactionByOrder(orderDetailDto.getOrderId(), description);
        processedRows = processedRefRows + processedRows;
        log.debug("rows after refs {}", processedRows);
        /**/
        if (!transactionService.setStatusById(
            orderDetailDto.getTransactionId(),
            TransactionStatus.DELETED.getStatus())) {
          return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
        }
        /**/
        processedRows++;
      } else if (currentOrderStatus == OrderStatus.OPENED) {
        WalletTransferStatus walletTransferStatus = walletService.walletInnerTransfer(
            orderDetailDto.getOrderCreatorReservedWalletId(),
            orderDetailDto.getOrderCreatorReservedAmount(),
            TransactionSourceType.ORDER,
            orderId,
            description);
        if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
          return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
        }
        /**/
        if (!transactionService.setStatusById(
            orderDetailDto.getTransactionId(),
            TransactionStatus.DELETED.getStatus())) {
          return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
        }
      }
    }
    if (currentOrderStatus.equals(OrderStatus.OPENED)) {
        eventPublisher.publishEvent(new CancelOrderEvent(getOrderById(orderId), true));
    }
    return processedRows;
  }


  private int unprocessReferralTransactionByOrder(int orderId, String description) {
    List<Transaction> transactions = transactionService.getPayedRefTransactionsByOrderId(orderId);
    for (Transaction transaction : transactions) {
      WalletTransferStatus walletTransferStatus = null;
      try {
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setWalletId(transaction.getUserWallet().getId());
        walletOperationData.setAmount(transaction.getAmount());
        walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
        walletOperationData.setCommission(transaction.getCommission());
        walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
        walletOperationData.setSourceType(TransactionSourceType.REFERRAL);
        walletOperationData.setSourceId(transaction.getSourceId());
        walletOperationData.setDescription(description);
        walletOperationData.setOperationType(OperationType.OUTPUT);
        walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        referralService.setRefTransactionStatus(ReferralTransactionStatusEnum.DELETED, transaction.getSourceId());
        companyWalletService.substractCommissionBalanceById(transaction.getCompanyWallet().getId(), transaction.getAmount().negate());
      } catch (Exception e) {
        log.error("error unprocess ref transactions" + e);
      }
      log.debug("status " + walletTransferStatus);
      if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
        throw new RuntimeException("can't unprocess referral transaction for order " + orderId);
      }
    }
    log.debug("end unprocess refs ");
    return transactions.size();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles) {
    return orderDao.getUserSummaryOrdersByCurrencyPairList(requesterUserId, startDate, endDate, roles);
  }

  @Transactional(readOnly = true)
  @Override
  public String getOrdersForRefresh(Integer pairId, OperationType operationType, UserRole userRole) {
    CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
    List<OrderListDto> dtos;
    switch (operationType) {
      case BUY: {
        dtos = getAllBuyOrdersEx(cp, Locale.ENGLISH, userRole);
        break;
      }
      case SELL: {
        dtos = getAllSellOrdersEx(cp, Locale.ENGLISH, userRole);
        break;
      }
      default: return null;
    }
    try {
      return objectMapper.writeValueAsString(new OrdersListWrapper(dtos, operationType.name(), pairId));
    } catch (JsonProcessingException e) {
      log.error(e);
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public String getTradesForRefresh(Integer pairId, String email, RefreshObjectsEnum refreshObjectEnum) {
    CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
    List<OrderAcceptedHistoryDto> dtos = this.getOrderAcceptedForPeriodEx(email,
            new BackDealInterval("24 HOUR"),
            100,
            cp,
            Locale.ENGLISH);
    try {
      return new JSONArray(){{put(objectMapper.writeValueAsString(new OrdersListWrapper(dtos, refreshObjectEnum.name(), pairId)));}}.toString();
    } catch (JsonProcessingException e) {
      log.error(e);
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public String getAllAndMyTradesForInit(int pairId, Principal principal) throws JsonProcessingException {
    CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
    List<OrderAcceptedHistoryDto> dtos = this.getOrderAcceptedForPeriodEx(null,
            new BackDealInterval("24 HOUR"),
            100,
            cp,
            Locale.ENGLISH);
    JSONArray jsonArray = new JSONArray(){{
      put(objectMapper.writeValueAsString(new OrdersListWrapper(dtos, RefreshObjectsEnum.ALL_TRADES.name(), pairId)));
    }};
    if (principal != null) {
      List<OrderAcceptedHistoryDto> myDtos = this.getOrderAcceptedForPeriodEx(principal.getName(),
              new BackDealInterval("24 HOUR"),
              100,
              cp,
              Locale.ENGLISH);
      jsonArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(myDtos, RefreshObjectsEnum.MY_TRADES.name(), pairId)));
    }
    return jsonArray.toString();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(CurrencyPair currencyPair, OperationType operationType) {
    return orderDao.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair.getId(), operationType.getType());
  }

  @Transactional
  @Override
  public String getChartData(Integer currencyPairId, final BackDealInterval backDealInterval) {
    CurrencyPair cp = currencyService.findCurrencyPairById(currencyPairId);
    List<CandleChartItemDto> rows = this.getDataForCandleChart(cp, backDealInterval);
    ArrayList<List> arrayListMain = new ArrayList<>();
        /*in first row return backDealInterval - to synchronize period menu with it*/
    arrayListMain.add(new ArrayList<Object>() {{
      add(backDealInterval);
    }});
    for (CandleChartItemDto candle : rows) {
      ArrayList<Object> arrayList = new ArrayList<>();
                /*values*/
      arrayList.add(candle.getBeginDate().toString());
      arrayList.add(candle.getEndDate().toString());
      arrayList.add(candle.getOpenRate());
      arrayList.add(candle.getCloseRate());
      arrayList.add(candle.getLowRate());
      arrayList.add(candle.getHighRate());
      arrayList.add(candle.getBaseVolume());
      arrayListMain.add(arrayList);
    }

    try {
      return objectMapper.writeValueAsString(new OrdersListWrapper(arrayListMain,
              backDealInterval.getInterval(), currencyPairId));
    } catch (JsonProcessingException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  public String getAllCurrenciesStatForRefresh() {
    OrdersListWrapper wrapper = new OrdersListWrapper(this.getOrdersStatisticByPairsEx(),
                                                      RefreshObjectsEnum.CURRENCIES_STATISTIC.name());
    try {
      return new JSONArray(){{put(objectMapper.writeValueAsString(wrapper));}}.toString();
    } catch (JsonProcessingException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  public String getSomeCurrencyStatForRefresh(List<Integer> currencyIds) {
    OrdersListWrapper wrapper = new OrdersListWrapper(this.getStatForSomeCurrencies(currencyIds),
            RefreshObjectsEnum.CURRENCY_STATISTIC.name());
    try {
      return new JSONArray(){{put(objectMapper.writeValueAsString(wrapper));}}.toString();
    } catch (JsonProcessingException e) {
      log.error(e);
      return null;
    }
  }

  @Override
  public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForPeriod(LocalDateTime startTime, LocalDateTime endTime,
                                                                              List<Integer> userRoleIdList) {
    return orderDao.getCurrencyPairTurnoverForPeriod(startTime, endTime, userRoleIdList);
  }



}


