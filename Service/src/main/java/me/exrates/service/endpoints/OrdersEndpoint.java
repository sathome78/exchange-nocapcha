package me.exrates.service.endpoints;









import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.OrderType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maks on 02.08.2017.
 */
/*
@Log4j2
@ServerEndpoint(value="/public_sockets/{currencyPairName}",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class )
public class OrdersEndpoint {

    private List<Session> sessions = new  CopyOnWriteArrayList<>();

    public static Map<String, OrdersEndpoint> endpoints = new ConcurrentHashMap<>();

    CurrencyPair currencyPair;

    @Autowired
    private OrderService orderService;
    @Autowired
    private CurrencyService currencyService;
    private static final String LOCALE_KEY = "userLocale";


    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("currencyPair") String pairName) throws IOException, EncodeException {
        log.debug("open new session for {}", pairName);
        if (!endpoints.containsKey(pairName)) {
            currencyPair = currencyService.getCurrencyPairByName(pairName);
            if (currencyPair == null) {
                session.getBasicRemote().sendObject(pairName + " not exist");
                session.close();
                return;
            }
            endpoints.put(pairName, this);
        }
        sessions.add(session);
        try {
            session.getBasicRemote().sendObject(new OrdersListWrapper
                    (orderService.getAllBuyOrders(currencyPair, getExposedLocale(session)), "init", OrderType.BUY.getType()));
            broadcast(new OrdersListWrapper
                    (orderService.getAllSellOrders(currencyPair, getExposedLocale(session)), "init", OrderType.SELL.getType()));
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }


    @OnMessage
    public void onMessage(Session session, String message)
            throws IOException {
        */
/*on message recieve*//*

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    public void broadcast(Object message)
            throws IOException, EncodeException {
        sessions.forEach(s -> {
            synchronized (s) {
                try {
                    s.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Locale getExposedLocale(Session session)
    {
        */
/*return (Locale)session.getUserProperties().get(LOCALE_KEY);*//*

        return Locale.ENGLISH;
    }


}
*/
