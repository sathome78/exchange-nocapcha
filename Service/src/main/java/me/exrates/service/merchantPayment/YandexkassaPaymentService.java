package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.MerchantService;
import me.exrates.service.YandexKassaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("YandexkassaPaymentService")
public class YandexkassaPaymentService implements MerchantPaymentService {
    private static final String API_URL = "http://din24.net/index.php?route=acc/success/order";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private YandexKassaService yandexKassaService;

    private static final Logger LOGGER = LogManager.getLogger("merchant");

    /*@Override
    @Transactional
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
    }*/

    @Override
    @Transactional
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return yandexKassaService.preparePayment(creditsOperation, email);
    }
}
