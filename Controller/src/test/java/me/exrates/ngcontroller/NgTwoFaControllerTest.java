package me.exrates.ngcontroller;

import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.security.config.ApiSecurityConfig;
import me.exrates.security.service.NgUserService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//https://www.baeldung.com/integration-testing-in-spring
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AngularAppTestConfig.class, ApiSecurityConfig.class})
@WebAppConfiguration
public class NgTwoFaControllerTest extends AngularApiCommonTest {

    @Autowired
    private UserService userService;

    @Autowired
    private NgUserService ngUserService;

    @Autowired
    private G2faService g2faService;

    @InjectMocks
    private NgTwoFaController ngTwoFaController;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final String BASE_URL = "/api/private/v2/2FaOptions";

    @Before
    public void setUp() {
        ngTwoFaController = new NgTwoFaController(userService, g2faService, ngUserService);

        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("GUEST","USERNAME", AuthorityUtils
                .createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        HandlerExceptionResolver resolver = ((HandlerExceptionResolverComposite) wac
                .getBean("handlerExceptionResolver"))
                .getExceptionResolvers()
                .get(0);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngTwoFaController)
                .setHandlerExceptionResolvers(resolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void getSecurityCode_not_authorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/hash")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errorCode", is("MISSING_AUTHENTICATION_TOKEN")));
    }

    @Test
    public void getSecurityCode() throws Exception {
        Generic2faResponseDto dto = new Generic2faResponseDto("message", "code", "error");

        when(g2faService.getGoogleAuthenticatorCodeNg(anyInt())).thenReturn(dto);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/hash")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", is("message")))
                .andExpect(jsonPath("$.code", is("code")))
                .andExpect(jsonPath("$.error", is("error")));

        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).getGoogleAuthenticatorCodeNg(anyInt());
    }

    @Test
    public void getSecurityPinCode_not_authorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getSecurityPinCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).sendGoogleAuthPinConfirm(anyObject(), anyObject());
    }

    @Test
    public void submitGoogleSecret_not_authorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/submit")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void submitGoogleSecret_google_secret_invalid() throws Exception {
        when(g2faService.submitGoogleSecret(anyObject(), anyObject())).thenReturn(Boolean.FALSE);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/submit")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());


        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).sendGoogleAuthPinConfirm(anyObject(), anyObject());
    }

    @Test
    public void submitGoogleSecret_google_secret_ok() throws Exception {
        when(g2faService.submitGoogleSecret(anyObject(), anyObject())).thenReturn(Boolean.TRUE);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/submit")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());


        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).sendGoogleAuthPinConfirm(anyObject(), anyObject());
    }

    @Test
    public void disableGoogleAuthentication_not_authorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/google2fa/disable")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void verifyGoogleAuthenticatorCode_not_authorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/verify_google2fa")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}