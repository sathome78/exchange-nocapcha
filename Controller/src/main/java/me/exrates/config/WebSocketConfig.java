package me.exrates.config;

import me.exrates.controller.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(chatENWebSocketHandler(), "/chat-en").withSockJS();
        registry.addHandler(chatRUWebSocketHandler(), "/chat-ru").withSockJS();
        registry.addHandler(chatCNWebSocketHandler(), "/chat-cn").withSockJS();
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
