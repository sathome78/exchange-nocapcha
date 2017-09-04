package me.exrates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.UserRoleSettings;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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
@Log4j2
@Controller
@MessageMapping("topic")
public class WsContorller {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;


    @SubscribeMapping("trade_orders.{currencyPairId}")
    public String subscribeTradeOrders(@DestinationVariable Integer currencyPairId) throws Exception {
        log.debug("pair " + currencyPairId);
        return initOrders(currencyPairId, null);
    }

    @SubscribeMapping("trade_orders.f.{currencyPairId}")
    public String subscribeTradeOrdersFiltered(@DestinationVariable Integer currencyPairId, Principal principal) throws Exception {
        log.debug("filtered_pair " + currencyPairId);
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        return initOrders(currencyPairId, role);
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
