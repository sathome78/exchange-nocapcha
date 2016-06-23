package me.exrates.config;

import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.enums.ChatLang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

import java.util.EnumMap;
import java.util.stream.Stream;

import static me.exrates.model.enums.ChatLang.CN;
import static me.exrates.model.enums.ChatLang.EN;
import static me.exrates.model.enums.ChatLang.RU;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EnumMap<ChatLang, ChatWebSocketHandler> handlers;

    @Autowired
    public WebSocketConfig(EnumMap<ChatLang, ChatWebSocketHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(handlers.get(EN), "/chat-en").withSockJS();
        registry.addHandler(handlers.get(RU), "/chat-ru").withSockJS();
        registry.addHandler(handlers.get(CN), "/chat-cn").withSockJS();
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
}
