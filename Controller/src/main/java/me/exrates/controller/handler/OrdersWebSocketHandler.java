package me.exrates.controller.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.listener.StoreSessionListener;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.util.BiTuple;
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

import javax.servlet.http.HttpSession;
import javax.sound.sampled.SourceDataLine;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
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

    private Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreSessionListener sessionListener;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("open session {}", session.isOpen());
        URI uri = session.getUri();
        Optional<HttpSession> httpSession = sessionListener.getSessionById(getParamValue(uri.toString(), "session_id"));
        log.debug("open session {}", uri.toString());
        sessions.put(session.getId(), httpSession.orElseThrow(null));
    }

     @Override
     public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        onConnectionClose(session);
     }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("inc message {}", message.getPayload());
        try {
            HttpSession httpSession = sessions.get(session.getId());
        /*handle if htpSeesion null*/
            switch (message.getPayload()) {
                case "trading:newCurrencyPair" : {
                    CurrencyPair currencyPair = (CurrencyPair)httpSession.getAttribute("currentCurrencyPair");
                    log.debug("curr pair {}", currencyPair);
                    setCurrencyPairSessions(currencyPair, session);
                    initOrders(currencyPair, session);
                    break;
                }
                default:{

                }
            }
        } catch (Exception e) {
            log.error(e);
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
      /*  log.debug("init orders {}", currencyPair.getName());
        broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrdersEx
                (currencyPair, Locale.ENGLISH, false), "init", "orders", OperationType.BUY.name())),
                new ArrayList<WebSocketSession>(){{add(socketSession);}});
        broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllSellOrdersEx
                        (currencyPair, Locale.ENGLISH, false), "init", "orders", OperationType.SELL.name())),
                new ArrayList<WebSocketSession>(){{add(socketSession);}});*/
    }


    @Override
    public void refreshAllPairs() {
    /*    log.debug("refresh Pairs");
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        if (currencyPairs != null && !currencyPairs.isEmpty()) {
            currencyPairs.forEach(p -> {
                List<WebSocketSession> list = getListByPairs(p.getName());
                if (list != null && !list.isEmpty()) {
                  try {
                    broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrdersEx
                            (p, Locale.ENGLISH, false), "refresh", "orders", OperationType.BUY.name())), list);
                    broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllSellOrdersEx
                            (p, Locale.ENGLISH, false), "refresh", "orders", OperationType.SELL.name())), list);
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
            });
        }*/
    }

    @Override
    public void refreshPair(OperationType type, CurrencyPair currencyPair) {
       /* log.debug("refresh Pairs");
        List<WebSocketSession> list = getListByPairs(currencyPair.getName());
        if (list != null && !list.isEmpty()) {
            try {
                List<OrderListDto> orderList = new ArrayList<>();
                if (type.equals(OperationType.BUY)) {
                    orderList = orderService.getAllBuyOrdersEx
                            (currencyPair, Locale.ENGLISH, false);
                } else if (type.equals(OperationType.SELL)) {
                    orderList = orderService.getAllSellOrdersEx
                            (currencyPair, Locale.ENGLISH, false);
                }
                broadcast(objectMapper.writeValueAsString(new OrdersListWrapper(
                        orderList, "refresh", "orders", type.name())), list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void broadcast(String message, List<WebSocketSession> wsSessions) throws IOException, EncodeException {
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
