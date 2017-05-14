package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.service.MerchantService;
import me.exrates.service.NixMoneyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

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

    /*@Override
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
    }*/

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
