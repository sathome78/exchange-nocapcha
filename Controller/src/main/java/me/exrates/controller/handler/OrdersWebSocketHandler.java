package me.exrates.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.OrderType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maks on 07.08.2017.
 */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Log4j2
public class OrdersWebSocketHandler extends TextWebSocketHandler implements OrdersHandler {

    private Map<String, List<WebSocketSession>> currencyPairSessions = new ConcurrentHashMap<>();

    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("open session {}", session.isOpen());
        session.sendMessage(new TextMessage("hello"));
        sessions.add(session);
    }

     @Override
     public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        onConnectionClose(session);
     }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("inc message {}", message.getPayload());
        switch (message.getPayload()) {
            case "trading:newCurrencyPair" : {
                CurrencyPair currencyPair = currencyService.getCurrencyPairByName("EDR/BTC");
                setCurrencyPairSessions(currencyPair, session);
                initOrders(currencyPair, session);
                break;
            }
            default:{

            }
        }
    }

    @Override
    public List<WebSocketSession> getListByPairs(String pair) {
            return currencyPairSessions.get(pair);
    }

    private void onConnectionClose(WebSocketSession session) {
        sessions.remove(session);
        String pair = session.getAttributes().get("pair").toString();
        currencyPairSessions.get(pair).remove(session);

    }

    private void setCurrencyPairSessions(CurrencyPair currencyPair, WebSocketSession session) throws IOException {
        if (currencyPair == null) {
            session.sendMessage(new TextMessage("pair not exist"));
            session.close();
            return;
        }
        synchronized (this) {
            if (currencyPairSessions.get(currencyPair.getName()) == null) {
                currencyPairSessions.put(currencyPair.getName(), new ArrayList<WebSocketSession>(){{add(session);}});
                log.debug(currencyPairSessions.get(currencyPair.getName()).size());
            } else {
                currencyPairSessions.get(currencyPair.getName()).add(session);
                log.debug(currencyPairSessions.get(currencyPair.getName()).size());
            }
        }
        session.getAttributes().put("pair", currencyPair.getName());
    }

    private String getParamValue(String link, String paramName) throws URISyntaxException {
        List<NameValuePair> queryParams = new URIBuilder(link).getQueryParams();
        return queryParams.stream()
                .filter(param -> param.getName().equalsIgnoreCase(paramName))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse("");
    }

    private void initOrders(CurrencyPair currencyPair, WebSocketSession socketSession) throws IOException, EncodeException {
        log.debug("init orders {}", currencyPair.getName());
        broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrders
                (currencyPair, Locale.ENGLISH), "refresh", OrderType.BUY.getType())),
                new ArrayList<WebSocketSession>(){{add(socketSession);}});
        broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrders
                        (currencyPair, Locale.ENGLISH), "refresh", OrderType.SELL.getType())),
                new ArrayList<WebSocketSession>(){{add(socketSession);}});
    }


    @Scheduled(fixedDelay = 6000)
    public void refreshPairs() {
        log.debug("refresh Pairs");
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        if (currencyPairs != null && !currencyPairs.isEmpty()) {
            currencyPairs.forEach(p -> {
                List<WebSocketSession> list = getListByPairs(p.getName());
                if (list != null && !list.isEmpty()) {
                  try {
                    broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrders
                            (p, Locale.ENGLISH), "refresh", OrderType.BUY.getType())), list);
                    broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllSellOrders
                            (p, Locale.ENGLISH), "refresh", OrderType.SELL.getType())), list);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
            });
        }
    }

    @Override
    public void broadcast(String message, List<WebSocketSession> wsSessions)
            throws IOException, EncodeException {
        wsSessions.forEach(s -> {
            synchronized (s) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
