package me.exrates.config;

import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.enums.ChatLang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.EnumMap;

import static me.exrates.model.enums.ChatLang.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EnumMap<ChatLang, ChatWebSocketHandler> handlers;

    @Autowired
    public WebSocketConfig(final EnumMap<ChatLang, ChatWebSocketHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        //TODO temporary disable
        registry.addHandler(handlers.get(EN), "/chat-en").withSockJS();
        registry.addHandler(handlers.get(RU), "/chat-ru").withSockJS();
        registry.addHandler(handlers.get(CN), "/chat-cn").withSockJS();
        registry.addHandler(handlers.get(AR), "/chat-ar").withSockJS();
    }

    @Bean(name = "chatEN")
    public ChatWebSocketHandler chatENWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatRU")
    public ChatWebSocketHandler chatRUWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatCN")
    public ChatWebSocketHandler chatCNWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Bean(name = "chatAR")
    public ChatWebSocketHandler chatARWebSocketHandler() {
        return new ChatWebSocketHandler();
    }
}
