package me.exrates.controller;

import me.exrates.controller.merchants.CommonMerchantsController;
import me.exrates.model.Currency;
import me.exrates.service.CurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonMerchantsControllerTest {

    private MockMvc mockMvc;

    @Mock
    CurrencyService currencyService;

    @InjectMocks
    CommonMerchantsController commonMerchantsController;

    @Before
    public void setup() {
        Currency currency = new Currency();
        currency.setName("mockName");
        currency.setDescription("mockDescription");
        List<Currency> mockResult = new ArrayList<>();
        mockResult.add(currency);
        when(currencyService.getAllCurrencies()).thenReturn(mockResult);
        mockMvc = MockMvcBuilders.standaloneSetup(commonMerchantsController).build();
    }

    @Test
    public void testYandexMoneyMerchError() throws Exception {
        mockMvc.perform(get("/merchants/yandexmoney/error")
        .param("error","not enough money"))
        .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    ModelAndView modelAndView = mvcResult.getModelAndView();
                    ModelAndViewAssert.assertViewName(modelAndView,"merchanterror");
                });
    }
}