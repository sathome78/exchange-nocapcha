package me.exrates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.Locale;

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

    @SubscribeMapping("trade_orders.{currencyPairId}")
    public String getPositions(@DestinationVariable Integer currencyPairId) throws Exception {
        log.debug("pair " + currencyPairId);
        return initOrders(currencyPairId);
    }

    private String initOrders(Integer currencyPair) throws IOException, EncodeException {
        log.debug("init orders {}", currencyPair);
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPair);
        if (cp == null) {
            return null;
        }
        JSONArray objectsArray = new JSONArray();
        objectsArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllSellOrdersEx
                (cp, Locale.ENGLISH, false), OperationType.SELL.name())));
        objectsArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrdersEx
                (cp, Locale.ENGLISH, false), OperationType.BUY.name())));
        return objectsArray.toString();

    }

}
