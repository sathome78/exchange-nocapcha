package me.exrates.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.events.OrderEvent;
import me.exrates.service.vo.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@Component
@PropertySource(value = {"classpath:/job.properties"})
public class OrdersEventHandleService {

    @Autowired
    private ExchangeRatesHolder ratesHolder;
    @Autowired
    private CurrencyStatisticsHandler currencyStatisticsHandler;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;


    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    private Map<Integer, TradesEventsHandler> mapTrades = new ConcurrentHashMap<>();
    private Map<Integer, MyTradesHandler> mapMyTrades = new ConcurrentHashMap<>();
    private Map<Integer, ChartRefreshHandler> mapChart = new ConcurrentHashMap<>();


    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(OrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        log.debug("order event {} ", exOrder);
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(CreateOrderEvent event) {
        log.debug("new thr create {} ", Thread.currentThread().getName());
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(AcceptOrderEvent event) {
        log.debug("new thr accept {} ", Thread.currentThread().getName());
        ExOrder order = (ExOrder) event.getSource();
        handleAllTrades(order);
        handleMyTrades(order);
        handleChart(order);
        ratesHolder.onRatesChange(order.getCurrencyPairId(), order.getExRate());
        currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
    }

    @Async
    @TransactionalEventListener
    void handleCallback(AcceptOrderEvent event) throws JsonProcessingException {
        if (!userHasAuthority(UserRole.TRADER)) return;
        ExOrder source = (ExOrder) event.getSource();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = userService.getCallBackUrlByEmail(email,source.getCurrencyPairId());

        processCallBackUrl(event, email, url);
    }

    private void processCallBackUrl(AcceptOrderEvent event, String email, String url) throws JsonProcessingException {
        if (url != null) {
            CallBackLogDto callBackLogDto = makeCallBack((ExOrder) event.getSource(), url);
            callBackLogDto.setRequestJson(new ObjectMapper().writeValueAsString(event));
            callBackLogDto.setUserId(userService.getIdByEmail(email));
            orderService.logCallBackData(callBackLogDto);
            log.info("*** Callback. User email:" + email + " | Callback:" + callBackLogDto);
        } else {
            log.info("*** Callback url wasn't set. User email:" + email);
        }
    }

    private boolean userHasAuthority(UserRole authority) {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.toString().equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    private CallBackLogDto makeCallBack(ExOrder order, String url) throws JsonProcessingException {
        CallBackLogDto callbackLog = new CallBackLogDto();
        callbackLog.setRequestDate(LocalDateTime.now());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, new ObjectMapper().writeValueAsString(order), String.class);
        callbackLog.setResponseCode(responseEntity.getStatusCodeValue());
        callbackLog.setResponseJson(responseEntity.getBody());
        callbackLog.setResponseDate(LocalDateTime.now());
        return callbackLog;
    }

    private void onOrdersEvent(Integer pairId, OperationType operationType) {
        Map<Integer, OrdersEventsHandler> mapForWork;
        if (operationType.equals(OperationType.BUY)) {
            mapForWork = mapBuy;
        } else if (operationType.equals(OperationType.SELL)) {
            mapForWork = mapSell;
        } else {
            log.error("no such map");
            return;
        }
        OrdersEventsHandler handler = mapForWork
                .computeIfAbsent(pairId, k -> OrdersEventsHandler.init(pairId, operationType));
        handler.onOrderEvent();
    }

    @Async
    void handleAllTrades(ExOrder exOrder) {
        TradesEventsHandler handler = mapTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> TradesEventsHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }

    @Async
    void handleMyTrades(ExOrder exOrder) {
        MyTradesHandler handler = mapMyTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> MyTradesHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent(exOrder.getUserId());
        handler.onAcceptOrderEvent(exOrder.getUserAcceptorId());
    }

    @Async
    void handleChart(ExOrder exOrder) {
        ChartRefreshHandler handler = mapChart
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> ChartRefreshHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }

}
