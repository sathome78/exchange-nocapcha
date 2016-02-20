package me.exrates.service.impl;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization;
import com.yandex.money.api.net.OAuth2Session;
import me.exrates.dao.YandexMoneyMerchantDao;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.squareup.okhttp.MediaType.parse;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service("yandexMoneyService")
@PropertySource({"classpath:merchants.properties"})
public class YandexMoneyServiceImpl implements YandexMoneyService {

    private @Value("${yandexmoney.clientId}") String clientId;

    private @Value("${yandexmoney.token}") String token;

    private @Value("${yandexmoney.redirectURI}") String redirectURI;

    private @Value("${yandexmoney.companyWalletId}") String companyWalletId;

    private static final Logger logger = LogManager.getLogger(YandexMoneyServiceImpl.class);

    @Autowired
    private YandexMoneyMerchantDao yandexMoneyMerchantDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public List<String> getAllTokens() {
        return yandexMoneyMerchantDao.getAllTokens();
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        return yandexMoneyMerchantDao.getTokenByUserEmail(userEmail);
    }

    @Override
    public boolean addToken(String token, String email) {
        final int id = userService.getIdByEmail(email);
        return yandexMoneyMerchantDao.createToken(token,id);
    }

    @Override
    public boolean updateTokenByUserEmail(String newToken, String email) {
        return yandexMoneyMerchantDao.updateTokenByUserEmail(email,newToken);
    }

    @Override
    public boolean deleteTokenByUserEmail(String email) {
        return yandexMoneyMerchantDao.deleteTokenByUserEmail(email);
    }

    @Override
    public URI getTemporaryAuthCode() {
        final DefaultApiClient apiClient = new DefaultApiClient(clientId, true);
        final OAuth2Session session = new OAuth2Session(apiClient);
        final OAuth2Authorization oAuth2Authorization = session.createOAuth2Authorization();
        final com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        session.setDebugLogging(true);
        System.out.println(clientId);
        final byte[] params = oAuth2Authorization.getAuthorizeParams()
                .addScope(Scope.ACCOUNT_INFO)
                .addScope(Scope.PAYMENT_P2P)
                .setRedirectUri(redirectURI)
                .setResponseType(APPLICATION_FORM_URLENCODED.getType())
                .build();
        final Request request = new Request.Builder()
                .url(oAuth2Authorization.getAuthorizeUrl())
                .post(RequestBody.create(parse(APPLICATION_FORM_URLENCODED.getType()), params))
                .build();
        final Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            logger.fatal(e);
            throw new MerchantInternalException("YandexMoneyServiceInput");
        }
        return URI.create(response.header(HttpHeaders.LOCATION));
    }

    @Override
    public Optional<String> getAccessToken(String code) {
        final Token.Request request = new Token.Request(code, clientId, redirectURI);
        final OAuth2Session session = new OAuth2Session(new DefaultApiClient(clientId));
        final Token token;
        try {
            token = session.execute(request);
        } catch (IOException e) {
            logger.fatal(e);
            throw new MerchantInternalException("YandexMoneyServiceInput");
        } catch (InvalidRequestException | InvalidTokenException | InsufficientScopeException e) {
            logger.error(e);
            return Optional.empty();
        }
        return Optional.of(token.accessToken);
    }

    @Override
    public Optional<RequestPayment> requestPayment(String email, String token, ModelMap map) {
        final DefaultApiClient apiClient = new DefaultApiClient(clientId, true);
        final OAuth2Session oAuth2Session = new OAuth2Session(apiClient);
        oAuth2Session.setAccessToken(token);
        logger.info("Purchase " + map.get("amount") + map.get("currency")
                + " from " + email + ". Total transferred amount: " + map.get("sumToPay")
                + ", Commission: " + map.get("commission") + ", Amount to be credited to user wallet: " + map.get("amount"));
        final P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder(companyWalletId)
                .setAmount((BigDecimal) map.get("sumToPay"))
                .setComment("Purchase " + map.get("amount") + map.get("currency") + " at the S.E. Birzha")
                .create();
        final RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams);
        try {
            return Optional.of(oAuth2Session.execute(request));
        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<ProcessPayment> processPayment(String requestId,OAuth2Session oAuth2Session) {
//        final ProcessPayment processPayment;
//        try {
//            processPayment = oAuth2Session.execute(new ProcessPayment.Request(requestId));
//            Transaction transaction = new Transaction();
//            transaction.setAmount(((BigDecimal) paymentData.get("sumToPay")).doubleValue());
//            transaction.setCommissionId(commissionService.findCommissionByType(OperationType.INPUT).getId());
//            transaction.setTransactionType(Payment.TransactionType.INPUT);
//            transaction.setWalletId(walletId);
//            transaction.setDate(LocalDateTime.now());
//            transactionService.create(transaction);
//
//        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//        if (processPayment.status.equals(ProcessPayment.Status.SUCCESS)) {
//            final int idByEmail = userService.getIdByEmail(principal.getName());
//            final int currencyId = (Integer) paymentData.get("currency");
//            int walletId = walletService.getWalletId(idByEmail, currencyId);
//            if (walletId==0){
//                Wallet wallet = new Wallet();
//                wallet.setCurrencyId(currencyId);
//                wallet.setUserId(idByEmail);
//                walletId = walletService.createNewWallet(wallet);
//            }
//            walletService.setWalletABalance(walletId, ((BigDecimal) paymentData.get("amount")).doubleValue());
//            CompanyTransaction companyTransaction = new CompanyTransaction();
//            companyTransaction.setCurrencyId(currencyId);
//            companyTransaction.setDate(LocalDateTime.now());
//            companyTransaction.setMerchantId((Integer) paymentData.get("merchant"));
//            companyTransaction.setOperationTypeId(OperationType.INPUT.type);
//            companyTransaction.setSum((BigDecimal) paymentData.get("amount"));
//            companyTransaction.setWalletId(companyWalletService.findByCurrencyId(currencyId).getId());
//            companyTransaction = companyTransactionService.create(companyTransaction);
//            logger.info(companyTransaction);
//
//            logger.info(transaction.toString());
//            redir.addFlashAttribute("message","Спасибо! Вы успешно ввели "+((BigDecimal) paymentData.get("amount")).setScale(2,BigDecimal.ROUND_CEILING) + " " + walletService.getCurrencyName(currencyId));
//            return new ModelAndView("redirect:/mywallets");
//        } else if (processPayment.status.equals(ProcessPayment.Status.REFUSED)) {
//            switch (processPayment.error) {
//                case NOT_ENOUGH_FUNDS:
//                    redir.addFlashAttribute("error", "merchants.notEnoughMoney");
//                    return redirectToMerchantError;
//                case ACCOUNT_BLOCKED:
//                    return new ModelAndView("redirect:" + execute.accountUnblockUri);
//            }
//        }
//        redir.addFlashAttribute("error", "merchants.internalError");
//        return redirectToMerchantError;
        return null;
    }
}