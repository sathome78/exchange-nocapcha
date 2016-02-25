package me.exrates.service;

import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.net.OAuth2Session;
import me.exrates.model.CreditsOperation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    String getTemporaryAuthCode();

    Optional<String> getAccessToken(String code);

    Optional<RequestPayment> requestInputPayment(String token, CreditsOperation creditsOperation);

    Optional<RequestPayment> requestOutputPayment(String token, String destination, CreditsOperation creditsOperation);
}