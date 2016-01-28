package me.exrates.controller;

import me.exrates.controller.merchants.CommonMerchantsController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonMerchantsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    CommonMerchantsController commonMerchantsController;

    @Before
    public void setup() {
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