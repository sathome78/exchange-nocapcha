package me.exrates.service;

import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.net.OAuth2Session;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public interface YandexMoneyService {

    List<String> getAllTokens();

    String getTokenByUserEmail(String userEmail);

    boolean addToken(String token, String email);

    boolean updateTokenByUserEmail(String newToken, String email);

    boolean deleteTokenByUserEmail(String email);

    URI getTemporaryAuthCode();

    Optional<String> getAccessToken(String code);

    Optional<RequestPayment> requestPayment(String email, String token, ModelMap map);

    Optional<ProcessPayment> processPayment(String requestId, OAuth2Session auth2Session);
}