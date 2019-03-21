package me.exrates.ngcontroller;

import me.exrates.model.dto.PinOrderInfoDto;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgWithdrawControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/balances/withdraw";

    @Mock
    private CurrencyService currencyService;
    @Mock
    private G2faService g2faService;
    @Mock
    private InputOutputService inputOutputService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private SecureService secureService;
    @Mock
    private UserOperationService userOperationService;
    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;
    @Mock
    private WithdrawService withdrawService;
    @Mock
    private MerchantServiceContext merchantServiceContext;

    @InjectMocks
    NgWithdrawController ngWithdrawController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngWithdrawController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("guest", "test@test.ru",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void createWithdrawalRequest_UserOperationAccessException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.FALSE);
        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST ERROR MSG");

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("TEST ERROR MSG")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
    }

    @Test(expected = Exception.class)
    public void createWithdrawalRequest_RequestLimitExceededException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST ERROR MSG");
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyObject(), anyString());
    }

    @Test
    public void createWithdrawalRequest_forbidden() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto(""))))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
    }

    @Test
    public void createWithdrawalRequest_checkGoogle2faVerifyCode_IncorrectPinException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Incorrect Google 2FA oauth code: TEST_SECURITY_CODE")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }

    @Test
    public void createWithdrawalRequest_checkPin_IncorrectPinException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.FALSE);
        when(secureService.sendWithdrawPincode(anyObject())).thenReturn(getMockNotificationResultDto());

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Incorrect pin: TEST_SECURITY_CODE")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(secureService, times(1)).sendWithdrawPincode(anyObject());
    }

    @Test
    public void createWithdrawalRequest_isOk() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_VALUE");

        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(java.util.Optional.ofNullable(getMockCreditsOperation()));
        when(withdrawService.createWithdrawalRequest(anyObject(), anyObject())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("TEST_KEY")))
                .andExpect(jsonPath("$", hasValue("TEST_VALUE")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(inputOutputService, times(1))
                .prepareCreditsOperation(anyObject(), anyString(), anyObject());
    }

    @Test
    public void createWithdrawalRequest_InvalidAmountException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenThrow(InvalidAmountException.class);

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(inputOutputService, times(1))
                .prepareCreditsOperation(anyObject(), anyString(), anyObject());
    }

    //    TODO
    @Test
    public void outputCredits() {
    }

    @Test
    public void sendUserPinCode_created() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(secureService.sendWithdrawPinCode(anyObject(), anyString(), anyString()))
                .thenReturn(getMockNotificationResultDto());

        mockMvc.perform(post(BASE_URL + "/request/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new PinOrderInfoDto("TEST_NAME", BigDecimal.ZERO))))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(secureService, times(1)).sendWithdrawPinCode(anyObject(), anyString(), anyString());
    }

    @Test
    public void sendUserPinCode_isOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/request/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new PinOrderInfoDto("TEST_NAME", BigDecimal.ZERO))))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
    }

    @Test
    public void sendUserPinCode_badRequest() throws Exception {
        when(userService.findByEmail(anyString())).thenThrow(Exception.class);

        mockMvc.perform(post(BASE_URL + "/request/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new PinOrderInfoDto("TEST_NAME", BigDecimal.ZERO))))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void getCommissions_isOk_memo_empty() throws Exception {
        int userId = 100;
        Map<String, String> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_VALUE");

        when(userService.getIdByEmail(anyString())).thenReturn(userId);

        mockMvc.perform(get(BASE_URL + "/commission")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("amount", "100")
                .param("currency", "100")
                .param("merchant", "100"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
    }

    @Test
    public void getCommissions_isOk_memo_not_empty() throws Exception {
        int userId = 100;
        Map<String, String> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_VALUE");

        when(userService.getIdByEmail(anyString())).thenReturn(userId);

        mockMvc.perform(get(BASE_URL + "/commission")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("amount", "100")
                .param("currency", "100")
                .param("merchant", "100")
                .param("memo", "memo"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
    }
}