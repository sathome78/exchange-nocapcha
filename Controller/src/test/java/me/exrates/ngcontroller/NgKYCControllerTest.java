package me.exrates.ngcontroller;

import me.exrates.model.dto.kyc.DocTypeEnum;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;
import me.exrates.model.dto.kyc.responces.DispatchInfo;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.service.KYCService;
import me.exrates.service.KYCSettingsService;
import me.exrates.service.QuberaService;
import me.exrates.service.UserService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgKYCControllerTest extends AngularApiCommonTest {
    private final static String PUBLIC_KYC = "/api/public/v2/kyc";
    private final static String PRIVATE_KYC = "/api/private/v2/kyc/";

    @Mock
    private UserService userService;
    @Mock
    private KYCService kycService;
    @Mock
    private KYCSettingsService kycSettingsService;
    @Mock
    private QuberaService quberaService;

    @InjectMocks
    private NgKYCController ngKYCController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngKYCController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void callback_isOk() throws Exception {
        byte[] bytes = "body".getBytes(Charset.defaultCharset());
        Pair<String, EventStatus> mockStatusPair = new ImmutablePair<>("Signature", EventStatus.ACCEPTED);

        Mockito.when(kycService.checkResponseAndUpdateVerificationStatus(anyString(), anyString())).thenReturn(mockStatusPair);

        mockMvc.perform(MockMvcRequestBuilders.post(PUBLIC_KYC + "/callback")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Signature", "TEST")
                .content(bytes))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("ACCEPTED")));

        verify(kycService, times(1)).checkResponseAndUpdateVerificationStatus(anyString(), anyString());
    }

    @Test
    public void callback_not_found() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PUBLIC_KYC + "/callback")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound());

        verify(kycService, never()).checkResponseAndUpdateVerificationStatus(anyString(), anyString());
    }

    @Test
    public void callback1_isOk() throws Exception {
        String[] missingOptionalDocs = new String[5];

        KycStatusResponseDto dto = new KycStatusResponseDto();
        dto.setStatus("TEST_STATUS");
        dto.setErrorMsg("TEST_ERROR_MSG");
        dto.setMissingOptionalDocs(missingOptionalDocs);
        dto.setAnalysisResults(Collections.EMPTY_LIST);

        doNothing().when(quberaService).processingCallBack(anyString(), anyObject());

        mockMvc.perform(MockMvcRequestBuilders.post(PUBLIC_KYC + "/webhook/{referenceId}", "15")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(quberaService, times(1)).processingCallBack(anyString(), anyObject());
    }

    @Test
    public void getVerificationUrl_isOk() throws Exception {
        Mockito.when(kycService.getVerificationUrl(anyString(), anyString())).thenReturn("TEST_VERIFICATION_URL");

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/verification-url")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("language_code", "EN")
                .param("country_code", "USA"))
                .andExpect(status().isOk());

        verify(kycService, times(1)).getVerificationUrl(anyString(), anyString());
    }

    @Test
    public void getVerificationUrl_no_required_parameters() throws Exception {
        Mockito.when(kycService.getVerificationUrl(anyString(), anyString())).thenReturn("TEST_VERIFICATION_URL");

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/verification-url")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest());

        verify(kycService, never()).getVerificationUrl(anyString(), anyString());
    }

    @Test
    public void getCountries_isOk() throws Exception {
        Mockito.when(kycSettingsService.getCountriesDictionary())
                .thenReturn(Collections.singletonList(new KycCountryDto("USA", "EN")));

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/countries")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].countryName", is("USA")))
                .andExpect(jsonPath("$.[0].countryCode", is("EN")));

        verify(kycSettingsService, times(1)).getCountriesDictionary();
    }

    @Test
    public void getLanguages_isOk() throws Exception {
        Mockito.when(kycSettingsService.getLanguagesDictionary())
                .thenReturn(Collections.singletonList(new KycLanguageDto("EN", "EN")));

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/languages")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].languageName", is("EN")))
                .andExpect(jsonPath("$.[0].languageCode", is("EN")));

        verify(kycSettingsService, times(1)).getLanguagesDictionary();
    }

    @Test
    public void getShuftiProStatusKyc1_isOk() throws Exception {
        Mockito.when(kycService.getShuftiProKycStatus(anyString())).thenReturn("TEST_KYC_STATUS");

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("kyc_provider", "SHUFTI_PRO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("TEST_KYC_STATUS")))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(kycService, times(1)).getShuftiProKycStatus(anyString());
        verify(kycService, never()).getQuberaKycStatus(anyString());
    }

    @Test
    public void getShuftiProStatusKyc2_isOk() throws Exception {
        Mockito.when(kycService.getShuftiProKycStatus(anyString())).thenReturn("TEST_KYC_STATUS");

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("TEST_KYC_STATUS")))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(kycService, times(1)).getShuftiProKycStatus(anyString());
        verify(kycService, never()).getQuberaKycStatus(anyString());
    }

    @Test
    public void getQuberaStatusKyc_isOk() throws Exception {
        Mockito.when(kycService.getQuberaKycStatus(anyString())).thenReturn("TEST_KYC_STATUS");

        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("kyc_provider", "QUBERA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is("TEST_KYC_STATUS")))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(kycService, never()).getShuftiProKycStatus(anyString());
        verify(kycService, times(1)).getQuberaKycStatus(anyString());
    }

    @Test
    public void getUndefinedProviderStatusKyc_isOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PRIVATE_KYC + "/status")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("kyc_provider", "TEST_PROVIDER"))
                .andExpect(status().isOk());

        verify(kycService, never()).getShuftiProKycStatus(anyString());
        verify(kycService, never()).getQuberaKycStatus(anyString());
    }

//    @Test
//    public void startKycProcessing_isOk() throws Exception {
//        IdentityDataRequest mockIdentityDataRequest = new IdentityDataRequest();
//        mockIdentityDataRequest.setBirthDay(14);
//        mockIdentityDataRequest.setBirthMonth(9);
//        mockIdentityDataRequest.setBirthYear(1987);
//        mockIdentityDataRequest.setFirstNames(new String[3]);
//        mockIdentityDataRequest.setLastName("Dou");
//        mockIdentityDataRequest.setTypeDoc(DocTypeEnum.ID);
//        mockIdentityDataRequest.setCountry("Ukraine");
//
//        Mockito.when(kycService.startKyCProcessing(anyObject(), anyString()))
//                .thenReturn(
//                        new OnboardingResponseDto(
//                                "TEST_UID", "TEST_URL", "TEST_EXPIRATION_DATE", new DispatchInfo()
//                        )
//                );
//
//        mockMvc.perform(MockMvcRequestBuilders.post(PRIVATE_KYC + "/start")
//                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
//                .content(objectMapper.writeValueAsString(mockIdentityDataRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.uid", is("TEST_UID")))
//                .andExpect(jsonPath("$.data.url", is("TEST_URL")))
//                .andExpect(jsonPath("$.data.expirationDate", is("TEST_EXPIRATION_DATE")))
//                .andExpect(jsonPath("$.data.dispatchInfo.notificationType", is(nullValue())))
//                .andExpect(jsonPath("$.data.dispatchInfo.msg", is(nullValue())))
//                .andExpect(jsonPath("$.error", is(nullValue())));
//
//        verify(kycService, times(1)).startKyCProcessing(anyObject(), anyString());
//    }
}