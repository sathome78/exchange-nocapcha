package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.dto.mobileApiDto.ResponseContainer;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.InterkassaService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.util.OkHttpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("InterkassaPaymentService")
public class InterkassaPaymentService implements MerchantPaymentService {
    private static final String API_URL = "https://sci.interkassa.com/";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InterkassaService interkassaService;

    private static final Logger LOGGER = LogManager.getLogger("merchant");

    @Override
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setType(MerchantApiResponseType.POST);
        Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("currencyId", payment.getCurrency());
        urlParams.put("merchantId", payment.getMerchant());
        urlParams.put("amount", payment.getSum());
        StringJoiner paramJoiner = new StringJoiner("&");
        String rootUrl = request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort();
        urlParams.forEach((key, value) -> paramJoiner.add(key + "=" + value));
        dto.setData(rootUrl + "/api/payments/merchantRedirect?" + paramJoiner.toString());
        return dto;
    }

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return interkassaService.preparePayment(creditsOperation, email);
    }
}