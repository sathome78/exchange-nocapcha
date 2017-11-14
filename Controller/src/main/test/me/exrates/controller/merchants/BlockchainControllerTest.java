package me.exrates.controller.merchants;

import me.exrates.service.BlockchainService;
import me.exrates.service.MerchantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class BlockchainControllerTest {

   /* @Mock
    private MerchantService merchantService;

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private BlockchainController blockchainController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = standaloneSetup(blockchainController).build();
    }

    @Test
    public void paymentHandlerShouldReturnBadRequestWithNoInvoiceIdMessage() throws Exception {
        mockMvc.perform(get("/merchants/blockchain/payment/received"))
                .andExpect(handler().methodName("paymentHandler"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("text/plain; charset=utf-8"))
                .andExpect(content().string("No invoice id_presented"));
    }*/
}
