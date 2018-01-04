package me.exrates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.UserRoleSettings;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ChartsCache;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import javax.annotation.PostConstruct;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by Maks on 24.08.2017.
 */
@Log4j2(topic = "ws_stomp_log")
@Controller
public class WsContorller {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ChartsCache chartsCache;


    @SubscribeMapping("/ev/{sessionId}")
    public String subscribeEvents(@DestinationVariable String sessionId) {
        log.debug("sessionId {} subscribed", sessionId);
        return "ok";
    }

    @SubscribeMapping("/statistics")
    public String subscribeStatistic() {
        log.debug("sbs currencies stat ");
        return orderService.getAllCurrenciesStatForRefresh();
    }

    @SubscribeMapping("/queue/trade_orders/f/{currencyId}")
    public String subscribeOrdersFiltered(@DestinationVariable Integer currencyId, Principal principal) throws IOException, EncodeException {
        log.debug("subscribe filtered ");
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        return initOrders(currencyId, role);
    }


    @SubscribeMapping("/trades/{currencyPairId}")
    public String subscribeTrades(@DestinationVariable Integer currencyPairId, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Principal principal = headerAccessor.getUser();
        log.debug("allTrades " + currencyPairId);
        return orderService.getAllAndMyTradesForInit(currencyPairId, principal);
    }

    @SubscribeMapping("/charts/{currencyPairId}/{period}")
    public String subscribeChart(@DestinationVariable Integer currencyPairId, @DestinationVariable String period) throws Exception {
        log.debug("sbs chart {}, {}", currencyPairId, period);
        BackDealInterval backDealInterval = ChartPeriodsEnum.convert(period).getBackDealInterval();
        return chartsCache.getDataForPeriod(currencyPairId, backDealInterval.getInterval());
    }

    @SubscribeMapping("/trade_orders/{currencyPairId}")
    public String subscribeTradeOrders(@DestinationVariable Integer currencyPairId) throws Exception {
        log.debug("pair " + currencyPairId);
        return initOrders(currencyPairId, null);
    }


    private String initOrders(Integer currencyPair, UserRole userRole) throws IOException, EncodeException {
        log.debug("init orders {}", currencyPair);
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPair);
        if (cp == null) {
            return null;
        }
        JSONArray objectsArray = new JSONArray();
        objectsArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllSellOrdersEx
                (cp, Locale.ENGLISH, userRole), OperationType.SELL.name(), currencyPair)));
        objectsArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrdersEx
                (cp, Locale.ENGLISH, userRole), OperationType.BUY.name(), currencyPair)));
        return objectsArray.toString();
    }

}
