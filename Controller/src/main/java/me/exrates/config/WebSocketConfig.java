package me.exrates.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    List<WebSocketSession> list = new ArrayList<>();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(new Handler(), "/chat").withSockJS();
    }

    class Handler extends TextWebSocketHandler {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            System.out.println(message);
            list.forEach(webSocketSession -> {
                try {
                    webSocketSession.sendMessage(new TextMessage(message.getPayload()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            list.add(session);
        }
    }
}