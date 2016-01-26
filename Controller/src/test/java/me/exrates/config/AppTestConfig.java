package me.exrates.config;

import com.yandex.money.api.methods.Token;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal;

import static org.mockito.Mockito.when;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Configuration
public class AppTestConfig {

    @Bean
    YandexMoneyService yandexMoneyService() {
        YandexMoneyService mock = Mockito.mock(YandexMoneyService.class);
        when(mock.getTokenByUserEmail("mockPresentEmail")).thenReturn(new Token("mockToken",null));
        when(mock.getTokenByUserEmail("mockAbsentToken")).thenReturn(null);
        return mock;
    }

    @Bean
    Principal principal() {
        Principal mock = Mockito.mock(Principal.class);
        when(mock.getName()).thenReturn("test@email.com");
        return mock;
    }

    @Bean
    UserService userService() {
        UserService mock = Mockito.mock(UserService.class);
        when(mock.getIdByEmail("test@email.com")).thenReturn(1);
        return mock;
    }
}