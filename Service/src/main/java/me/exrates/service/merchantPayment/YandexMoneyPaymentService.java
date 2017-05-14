package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("YandexMoneyPaymentService")
@PropertySource("classpath:/merchants/yandexmoney.properties")
public class YandexMoneyPaymentService implements MerchantPaymentService {

    private @Value("${yandexmoney.apiRedirectURI}") String apiRedirectURI;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LogManager.getLogger("merchant");

    /*@Override
    @Transactional
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        int paymentId = yandexMoneyService.saveInputPayment(payment);
        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setType(MerchantApiResponseType.GET);
        String redirectURI = apiRedirectURI + "&paymentId=" + paymentId + "&userId=" + userService.getIdByEmail(email);
        dto.setData(yandexMoneyService.getTemporaryAuthCode(redirectURI));
        return dto;
    }*/

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
