package me.exrates.controller.handler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.List;

/**
 * Created by maks on 07.08.2017.
 */
public interface OrdersHandler {
    List<WebSocketSession> getListByPairs(String pair);


    void broadcast(String message, List<WebSocketSession> wsSessions)
            throws IOException, EncodeException;
}
