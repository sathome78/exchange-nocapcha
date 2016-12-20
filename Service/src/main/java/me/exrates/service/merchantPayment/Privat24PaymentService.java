package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.MerchantService;
import me.exrates.service.Privat24Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("Privat24PaymentService")
public class Privat24PaymentService implements MerchantPaymentService {
    private static final String API_URL = "https://api.privatbank.ua/p24api/ishop";
    private static final Logger LOGGER = LogManager.getLogger("merchant");

    @Autowired
    private HttpServletRequest request;


    @Autowired
    private MerchantService merchantService;

    @Autowired
    private Privat24Service privat24Service;

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
        return privat24Service.preparePayment(creditsOperation, email);
    }
}

