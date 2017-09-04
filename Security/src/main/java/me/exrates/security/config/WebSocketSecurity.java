package me.exrates.security.config;

import me.exrates.model.UserRoleSettings;
import me.exrates.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.stereotype.Controller;

import java.util.List;


/**
 * Created by Maks on 29.08.2017.
 */
@Configuration
public class WebSocketSecurity  extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Autowired
    private UserRoleService userRoleService;

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        List<UserRoleSettings> settings = userRoleService.retrieveSettingsForAllRoles();
        String[] roles = settings.stream()
                .filter(UserRoleSettings::isOrderAcceptionSameRoleOnly)
                .map(p->p.getUserRole().name())
                .toArray(String[]::new);
        /*-----------------------------------------------------------------------------------------------------------*/
        messages.nullDestMatcher().permitAll()
                .simpSubscribeDestMatchers("/app/topic.trade_orders.*").permitAll()
                .simpSubscribeDestMatchers("/app/topic.trade_orders.f.*").hasAnyAuthority(roles)
                .anyMessage().denyAll();

    }

}
