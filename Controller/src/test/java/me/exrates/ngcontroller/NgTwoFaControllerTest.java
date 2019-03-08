package me.exrates.ngcontroller;

import me.exrates.security.service.NgUserService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;

//https://www.baeldung.com/integration-testing-in-spring
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AngularAppTestConfig.class})
@WebAppConfiguration
public class NgTwoFaControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private NgUserService ngUserService;

    @Autowired
    private G2faService g2faService;

    private MockMvc mockMvc;

    @InjectMocks
    private NgTwoFaController ngTwoFaController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        ngTwoFaController = new NgTwoFaController(userService, g2faService, ngUserService);

        HandlerExceptionResolver resolver = ((HandlerExceptionResolverComposite) webApplicationContext
                .getBean("handlerExceptionResolver"))
                .getExceptionResolvers()
                .get(0);
        mockMvc = MockMvcBuilders.standaloneSetup(ngTwoFaController)
                .setHandlerExceptionResolvers(resolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void getSecurityCode() {
    }

    @Test
    public void getSecurityPinCode() {
    }

    @Test
    public void submitGoogleSecret() {
    }

    @Test
    public void disableGoogleAuthentication() {
    }

    @Test
    public void verifyGoogleAuthenticatorCode() {
    }
}