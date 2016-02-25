package me.exrates.controller;

import me.exrates.controller.merchants.MerchantsExceptionHandlingAdvice;
import me.exrates.controller.merchants.YandexMoneyMerchantController;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import me.exrates.service.exception.MerchantInternalException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class YandexMoneyMerchantControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private YandexMoneyMerchantController yandexMoneyMerchantController;

    @Mock
    private WebApplicationContext webApplicationContext;

    @Mock
    private UserService userService;

    @Mock
    private YandexMoneyService yandexMoneyService;

    @Mock
    private MerchantService merchantService;

    @Mock
    Principal principal;

    @Before
    public void setup () {
        final ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerBeanDefinition("advice",new RootBeanDefinition(MerchantsExceptionHandlingAdvice.class,null,null));
        exceptionHandlerExceptionResolver.setApplicationContext(applicationContext);
        exceptionHandlerExceptionResolver.afterPropertiesSet();
        when(principal.getName()).thenReturn("test@email.com");
        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(merchantService.prepareCreditsOperation(any(),anyString())).thenReturn(Optional.empty());
        mockMvc = MockMvcBuilders.standaloneSetup(yandexMoneyMerchantController)
                .setHandlerExceptionResolvers(exceptionHandlerExceptionResolver)
                .build();
    }

//    @Test
//    public void successTemporaryAuthorizationCodeRequest() throws Exception {
//        final String uri = "code";
//        when(yandexMoneyService.getTemporaryAuthCode()).thenReturn(URI.create(uri));
//        mockMvc.perform(get("/merchants/yandexmoney/token/authorization"))
//                .andExpect(handler().methodName("yandexMoneyTemporaryAuthorizationCodeRequest"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(uri));
//    }

    @Test
    public void failureTemporaryAuthorizationCodeRequest() throws Exception {
        when(yandexMoneyService.getTemporaryAuthCode()).thenThrow(new MerchantInternalException("YandexMoneyServiceInput"));
        mockMvc.perform(get("/merchants/yandexmoney/token/authorization"))
                .andExpect(handler().methodName("yandexMoneyTemporaryAuthorizationCodeRequest"))
                .andExpect(redirectedUrl("/merchants/input"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",equalTo("merchants.internalError")));
    }

    @Test
    public void successfulAccessTokenRequest() throws Exception {
        final String mockToken = "mockToken";
        final String mockCode = "mockCode";
        when(yandexMoneyService.getAccessToken(mockCode)).thenReturn(Optional.of(mockToken));
        mockMvc.perform(get("/merchants/yandexmoney/token/access").param("code",mockCode))
                .andExpect(handler().methodName("yandexMoneyAccessTokenRequest"))
                .andExpect(flash().attributeExists("token"))
                .andExpect(flash().attribute("token",equalTo(mockToken)));
    }

    @Test
    public void accessTokenRequestWithInternalMerchantException() throws Exception {
        final String mockCode = "mockCode";
        when(yandexMoneyService.getAccessToken(mockCode)).thenThrow(new MerchantInternalException("YandexMoneyServiceInput"));
        mockMvc.perform(get("/merchants/yandexmoney/token/access").param("code",mockCode))
                .andExpect(redirectedUrl("/merchants/input"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",equalTo("merchants.internalError")));
    }

    @Test
    public void accessTokenRequestWithErrorYandexMoneyResponse() throws Exception {
        final String mockCode = "mockCode";
        when(yandexMoneyService.getAccessToken(mockCode)).thenReturn(Optional.empty());
        mockMvc.perform(get("/merchants/yandexmoney/token/access").param("code",mockCode))
                .andExpect(redirectedUrl("/merchants/input"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error",equalTo("merchants.authRejected")));
    }


//    @Test
//    public void preparePaymentWithInvalidPayment() throws Exception {
//        mockMvc.perform(post("/merchants/yandexmoney/payment/prepare")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("currency","1")
//                .param("merchant","1")
//                .param("sum","0.1")
//                .sessionAttr("creditsOperation","creditsOperation")
//                .principal(principal))
//                .andExpect(handler().methodName("preparePayment"))
//                .andExpect(model().attributeHasFieldErrorCode("payment","operationType",equalTo("NotNull")))
//                .andExpect(redirectedUrl("/merchants/output"));
//    }
//
//    @Test
//    public void preparePaymentWithInvalidMinSumPayment() throws Exception {
//        mockMvc.perform(post("/merchants/yandexmoney/payment/prepare")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("currency","1")
//                .param("merchant","1")
//                .param("sum","0.1")
//                .param("operationType", String.valueOf(OperationType.INPUT))
//                .principal(principal))
//                .andExpect(handler().methodName("preparePayment"))
//                .andExpect(model().attributeHasFieldErrorCode("payment","operationType",equalTo("NotNull")))
//                .andExpect(redirectedUrl("/merchants/output"));
//    }
}