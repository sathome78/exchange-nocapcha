package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.NgUserService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

public abstract class AngularApiCommonTest {

    @Autowired
    NgUserService ngUserService;

    @Autowired
    G2faService g2faService;

    @InjectMocks
    NgTwoFaController ngTwoFaController;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserService userService;

    @Autowired
    UserOperationService userOperationService;

    MockMvc mockMvc;


    ObjectMapper objectMapper = new ObjectMapper();

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

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

}
