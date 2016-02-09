package me.exrates.controller;

import me.exrates.controller.merchants.YandexMoneyMerchantController;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private Principal principal;


    @Before
    public void setup() {
        when(principal.getName()).thenReturn("test@email.com");
        when(userService.getIdByEmail("test@email.com")).thenReturn(1);
        mockMvc = MockMvcBuilders.standaloneSetup(yandexMoneyMerchantController).build();
    }

    @Test
    public void test() throws Exception {
        mockMvc.perform(post("/merchants/yandexmoney/payment/prepare")
                .characterEncoding("UTF-8")
                .param("targetPayment","rub")
                .param("meansOfPayment","ymoney")
                .param("sum","100")
                .principal(principal))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
            ModelAndView modelAndView = mvcResult.getModelAndView();
                    System.out.println(modelAndView.getModelMap());
                    ModelAndViewAssert.assertViewName(modelAndView,"assertpayment");
                    ModelAndViewAssert.assertModelAttributeValue(modelAndView,"userId",1);
                    ModelAndViewAssert.assertModelAttributeValue(modelAndView,"amount",97.00);
                    ModelAndViewAssert.assertModelAttributeValue(modelAndView,"commission",3.00);
                    ModelAndViewAssert.assertModelAttributeValue(modelAndView,"sumToPay",100.00);
                });
    }
}