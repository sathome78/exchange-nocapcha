package me.exrates.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by Maks on 24.08.2017.
 */
@PropertySource(value = "classpath:/websocket.properties")
@Configuration
@EnableWebSocketMessageBroker
public class StompWsConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Value("${ws.origin}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        registry.addEndpoint("/public_socket").setAllowedOrigins(origins).withSockJS().setClientLibraryUrl("//cdn.jsdelivr.net/sockjs/1/sockjs.min.js");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        /*SimpleBrokerRegistration brokerRegistration = config.enableSimpleBroker("")*/
        config.setApplicationDestinationPrefixes("/app");
        config.setPathMatcher(new AntPathMatcher("."));

    }
}
