package me.exrates.controller.openAPI;

import com.google.common.collect.Sets;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.security.config.OpenApiSecurityConfig;
import me.exrates.security.service.OpenApiAuthService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.userOperation.UserOperationService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OpenApiOrderOldControllerTest.Config.class, OpenApiSecurityConfig.class})
@WebAppConfiguration
public class OpenApiOrderOldControllerTest extends OpenApiCommonTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Autowired
    private OpenApiAuthService openApiAuthService;

    @Autowired
    private MessageSource messageSource;

    private MockMvc mockMvc;


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(openApiAuthService.getUserByPublicKey(anyString(), anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(getTestUserDetails());
        when(userService.getUserEmailFromSecurityContext()).thenReturn("test@test.me");
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(userService.getPreferedLang(anyInt())).thenReturn("en");
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(true);
    }

    @Test
    public void createOrder() throws Exception {
        when(orderService.prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString()))
                .thenReturn(getFakeOrderCreationResultDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/openapi/v1/orders/create"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1))
                .prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString());
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void acceptOrder() {
    }

    @Test
    public void cancelOrder() {
    }

    @Test
    public void addCallback() {
    }

    @Test
    public void updateallback() {
    }

    @Test
    @Ignore
    public void openOrders() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/openapi/v1/orders/open/{order_type}")
                .queryParam("currency_pair","btc_usd")
                .build()
                .expand("SELL");

        String expected = "http://localhost:8080/openapi/v1/orders/open/SELL?currency_pair=btc_usd";
        assertEquals(expected, uriComponents.toUriString());

        mockMvc.perform(getOpenApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1))
                .prepareAndCreateOrderRest(anyString(), anyObject(), anyObject(), any(), anyString());
        verifyNoMoreInteractions(orderService);
    }

    private OrderCreationResultDto getFakeOrderCreationResultDto() {
        OrderCreationResultDto orderCreationResultDto = new OrderCreationResultDto();
        orderCreationResultDto.setCreatedOrderId(1000);
        orderCreationResultDto.setAutoAcceptedQuantity(1000);
        orderCreationResultDto.setPartiallyAcceptedAmount(BigDecimal.TEN);
        orderCreationResultDto.setPartiallyAcceptedOrderFullAmount(BigDecimal.TEN);
        return orderCreationResultDto;
    }

    private UserDetails getTestUserDetails() {
        Collection<GrantedAuthority> tokenPermissions = Sets.newHashSet();
        return new User("name", "password", true, false, false,
               false, tokenPermissions);
    }

    @Configuration
    static class Config {

        @Bean
        public OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }

        @Bean
        public UserService userService(){
            return Mockito.mock(UserService.class);
        }

        @Bean
        public UserOperationService userOperationService() {
            return Mockito.mock(UserOperationService.class);
        }

        @Bean
        public MessageSource messageSource() {
            return Mockito.mock(MessageSource.class);
        }

        @Bean
        public OpenApiAuthService openApiAuthService() {
            return Mockito.mock(OpenApiAuthService.class);
        }
    }
}