package me.exrates.ngcontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import me.exrates.security.service.AuthTokenService;
import me.exrates.service.UserService;
import me.exrates.service.userOperation.UserOperationService;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.Collection;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public abstract class AngularApiCommonTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    UserService userService;

    @Autowired
    UserOperationService userOperationService;

    MockMvc mockMvc;

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        when(authTokenService.getUserByToken(anyString())).thenReturn(getTestUserDetails());
        when(userService.getUserEmailFromSecurityContext()).thenReturn("test@test.me");
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(userService.getPreferedLang(anyInt())).thenReturn("en");
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(true);
    }

    protected RequestBuilder getApiRequestBuilder(URI uri, HttpMethod method, HttpHeaders httpHeaders, String content, String contentType) {
        HttpHeaders headers = createHeaders();
        if (httpHeaders != null) {
            headers.putAll(httpHeaders);
        }
        if (method.equals(HttpMethod.GET)) {
            System.out.println(MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType));
            return MockMvcRequestBuilders.get(uri).headers(headers).content(content).contentType(contentType);
        } else if (method.equals(HttpMethod.POST)) {
            return MockMvcRequestBuilders.post(uri).headers(headers).content(content).contentType(contentType);
        }
        throw new UnsupportedOperationException(String.format("Method: %s not supported", method.toString()));
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put(HEADER_SECURITY_TOKEN, ImmutableList.of("Test-Token"));
        return headers;
    }

    private UserDetails getTestUserDetails() {
        Collection<GrantedAuthority> tokenPermissions = Sets.newHashSet();
        return new User("name", "password", true,
                false, false, false, tokenPermissions);
    }

}
