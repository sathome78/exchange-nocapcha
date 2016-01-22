package me.exrates.controller;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.processes.PaymentProcess;
import com.yandex.money.api.utils.HttpHeaders;
import com.yandex.money.api.utils.Strings;
import me.exrates.YandexMoneyProperties;
import me.exrates.model.User;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
public class MerchantsController {

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private UserService userService;

    @Autowired
    private YandexMoneyProperties yandexMoneyProperties;

    @RequestMapping(value = "/merchants", method = RequestMethod.GET)
    public String getPage() {
        return "merchants";
    }

    @RequestMapping(value = "/yandexmoney",method =  RequestMethod.GET)
    public String redirectToYandexMoneyLogin() {
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.getClientId(),true);
        OAuth2Session session = new OAuth2Session(apiClient);
        OAuth2Authorization oAuth2Authorization = session.createOAuth2Authorization();
        com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        session.setDebugLogging(true);
        byte[] params = oAuth2Authorization.getAuthorizeParams()
                .addScope(Scope.ACCOUNT_INFO)
                .addScope(Scope.PAYMENT_P2P)
                .setRedirectUri(yandexMoneyProperties.getRedirectURI().trim())
                .setResponseType(yandexMoneyProperties.getResponseType().trim())
                .build();
        Request request = new Request.Builder()
                .url(oAuth2Authorization.getAuthorizeUrl())
                .post(RequestBody.create(yandexMoneyProperties.getMediaType(),params))
                .build();
        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            return "redirect:error";
        }
        return "redirect:"+response.header(HttpHeaders.LOCATION);
    }

    @RequestMapping(value = "/yandexauth")
    public String getAccessTokenFromServer (@RequestParam(value = "code",required = false) String code, Principal principal) {
        if (Strings.isNullOrEmpty(code)) {
            return "redirect:error";
        }
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.getClientId(),true);
        OAuth2Session session = new OAuth2Session(apiClient);
        com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        session.setDebugLogging(true);
        Request request = new Request.Builder()
                .url(apiClient.getHostsProvider().getMoney()+"/oauth/token")
                .post(RequestBody.create(yandexMoneyProperties.getMediaType(),"code="+code
                +"&client_id="+yandexMoneyProperties.getClientId()+"&grant_type=authorization_code&redirect_uri="+
                yandexMoneyProperties.getRedirectURI()))
                .build();
        Response response=null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            return "redirect:error";
        }
        String accessToken;
        try {
            accessToken = response.body().string();
        } catch (IOException e) {
            return "redirect:error";
        }
        System.out.println("TOKEN"+accessToken);
        String email = principal.getName();
        int idByEmail = userService.getIdByEmail(email);
        User user = new User();
        user.setId(idByEmail);
        Token token = new Token(accessToken.replace("\"","").replace("{","").replace("}","").split(":")[1],null); //// TODO: 1/22/16 need to refactor
        if (yandexMoneyService.addToken(token, user)) {
            return "redirect:processYandexMoneyPayment";
        }
        return "redirect:error";
    }

    @RequestMapping(value = "/processYandexMoneyPayment",method = RequestMethod.POST)
    public String processPayment(Principal principal) {
        String email = principal.getName();
        int idByEmail = userService.getIdByEmail(email);
        User user = new User();
        user.setId(idByEmail);
        Token tokenByUser = yandexMoneyService.getTokenByUser(user);
        if (tokenByUser==null) {
            return "redirect:yandexmoney";
        }
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.getClientId(),true);
        OAuth2Session oAuth2Session = new OAuth2Session(apiClient);
        oAuth2Session.setAccessToken(tokenByUser.accessToken);
        P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder("denis.savin.x@yandex.com")
                .setAmount(BigDecimal.valueOf(100L))
                .setComment("Test payment")
                .setLabel("Test payment")
                .create();
        RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams).setTestResult(RequestPayment.TestResult.SUCCESS);
        try {
            RequestPayment execute = oAuth2Session.execute(request);
            BaseRequestPayment.Status status = execute.status;
            System.out.println(status.getCode());
        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            e.printStackTrace();
        }
        return null;
    }
}