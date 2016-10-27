package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;

import java.util.Locale;
import java.util.Map;

/**
 * Created by OLEG on 05.09.2016.
 */
public interface MerchantPaymentService {

    MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale);

    Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale);
}
