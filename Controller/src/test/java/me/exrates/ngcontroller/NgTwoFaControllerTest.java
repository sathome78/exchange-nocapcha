package me.exrates.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.security.config.ApiSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

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

    private final String BASE_URL = "/api/private/v2/2FaOptions";

    @Before
    public void setUp() {
        ngTwoFaController = new NgTwoFaController(userService, g2faService, ngUserService);

        HandlerExceptionResolver resolver = ((HandlerExceptionResolverComposite) webApplicationContext
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
    public void getSecurityCode() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        Generic2faResponseDto dto = new Generic2faResponseDto("message", "code", "error");

        when(userService.getIdByEmail(anyString())).thenReturn(1);

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
    public void getSecurityPinCode() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());

        verify(g2faService, times(1)).sendGoogleAuthPinConfirm(anyObject(), anyObject());
    }

    @Test
    public void submitGoogleSecret_google_secret_invalid() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/submit")
                .build();

        when(g2faService.submitGoogleSecret(anyObject(), anyObject())).thenReturn(Boolean.FALSE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());


        verify(userService, times(0)).getIdByEmail(anyString());

        verify(g2faService, times(0)).sendGoogleAuthPinConfirm(anyObject(), anyObject());

        verify(ngUserService, times(0)).sendEmailDisable2Fa(anyString());
    }

    @Test
    public void submitGoogleSecret_google_secret_ok() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/submit")
                .build();

        Map<String, String> map = new HashMap<>();
        map.put(anyString(),anyString());

        String body = objectMapper.writeValueAsString(map);

        when(g2faService.submitGoogleSecret(any(User.class), anyMapOf(String.class,String.class))).thenReturn(Boolean.TRUE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, body, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());


        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).sendGoogleAuthPinConfirm(anyObject(), anyObject());

        verify(ngUserService, times(1)).sendEmailEnable2Fa(anyString());
    }

    @Test
    public void disableGoogleAuthentication() throws Exception {
    }

    @Test
    public void verifyGoogleAuthenticatorCode_isOk() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/verify_google2fa")
                .queryParam("code", "CODE")
                .build();

        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.TRUE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());


        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }

    @Test
    public void verifyGoogleAuthenticatorCode_invalid() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/verify_google2fa")
                .queryParam("code", "CODE")
                .build();

        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.FALSE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());


        verify(userService, times(1)).getIdByEmail(anyString());

        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }
}