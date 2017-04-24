package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.LiqpayService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("LiqPayPaymentService")
public class LiqPayPaymentService implements MerchantPaymentService {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private LiqpayService liqpayService;

    private static final Logger LOGGER = LogManager.getLogger("merchant");

    /*@Override
    @Transactional
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);

        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setType(MerchantApiResponseType.GET);
        RedirectView redirectView = liqpayService.preparePayment(creditsOperation, email);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.putAll(redirectView.getAttributesMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> {
                    try {
                        return Collections.singletonList(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new MerchantInternalException(e);
                    }
                })));
        dto.setData(UriComponentsBuilder.fromHttpUrl(redirectView.getUrl().substring(0, redirectView.getUrl().length()  - 1))
                .queryParams(queryParams)
                .build(true).toUriString());
        return dto;
    }*/

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
