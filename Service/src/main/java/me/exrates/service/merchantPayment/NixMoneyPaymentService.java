package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.MerchantService;
import me.exrates.service.NixMoneyService;
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
@Component("NixMoneyPaymentService")
public class NixMoneyPaymentService implements MerchantPaymentService {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private NixMoneyService nixMoneyService;

    private static final Logger LOGGER = LogManager.getLogger("merchant");

    @Override
    @Transactional
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);
        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setType(MerchantApiResponseType.GET);
        RedirectView redirectView = nixMoneyService.preparePayment(creditsOperation, email);
        StringJoiner params = new StringJoiner("&");
        redirectView.getAttributesMap().forEach((key, value) -> params.add(key + "=" + value.toString()));
        LOGGER.debug(params.toString());

        dto.setData(redirectView.getUrl() + "?" + params.toString());
        return dto;
    }

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
