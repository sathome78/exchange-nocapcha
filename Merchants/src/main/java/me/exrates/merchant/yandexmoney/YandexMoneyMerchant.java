package me.exrates.merchant.yandexmoney;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization;
import com.yandex.money.api.net.OAuth2Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.squareup.okhttp.MediaType.parse;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Component
@PropertySource({"classpath:merchants.properties"})
public class YandexMoneyMerchant {

    private @Value("${yandexmoney.clientId}") String clientId;

    private @Value("${yandexmoney.token}") String token;

    private @Value("${yandexmoney.redirectURI}") String redirectURI;

    private @Value("${yandexmoney.companyWalletId}") String companyWalletId;

    private static final Logger logger = LogManager.getLogger(YandexMoneyMerchant.class);


    public Optional<String> getTemporaryAuthCode() {
        DefaultApiClient apiClient = new DefaultApiClient(clientId, true);
        OAuth2Session session = new OAuth2Session(apiClient);
        OAuth2Authorization oAuth2Authorization = session.createOAuth2Authorization();
        com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        session.setDebugLogging(true);
        byte[] params = oAuth2Authorization.getAuthorizeParams()
                .addScope(Scope.ACCOUNT_INFO)
                .addScope(Scope.PAYMENT_P2P)
                .setRedirectUri(redirectURI)
                .setResponseType(APPLICATION_FORM_URLENCODED.getType())
                .build();
        Request request = new Request.Builder()
                .url(oAuth2Authorization.getAuthorizeUrl())
                .post(RequestBody.create(parse(APPLICATION_FORM_URLENCODED.getType()), params))
                .build();
        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            logger.fatal(e);
            return Optional.empty();
        }
        return Optional.of(response.header(HttpHeaders.LOCATION));
    }

    public Optional<String> getAccessToken(String code) {
        final Token.Request request = new Token.Request(code, clientId, redirectURI);
        OAuth2Session session = new OAuth2Session(new DefaultApiClient(clientId));
        Token token;
        try {
            token = session.execute(request);
        } catch (IOException | InvalidTokenException | InvalidRequestException | InsufficientScopeException e) {
            logger.error(e);
            return Optional.empty();
        }
        return Optional.of(token.accessToken);
    }



}