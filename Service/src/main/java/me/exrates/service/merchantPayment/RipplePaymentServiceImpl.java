package me.exrates.service.merchantPayment;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.EthereumService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.ripple.RippleService;
import me.exrates.service.ripple.RippledNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Created by maks on 08.05.2017.
 */

@Log4j2
@Component("RipplePaymentService ")
public class RipplePaymentServiceImpl implements MerchantPaymentService {

    @Autowired
    private RippleService rippleService;
    @Autowired
    private MerchantService merchantService;

    @Override
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);
        final String account = rippleService.createAddress(creditsOperation);
        final String notification = merchantService
                .sendDepositNotification(account,email ,locale, creditsOperation, "merchants.depositNotification.body");
        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setData(notification);
        dto.setQr(account + "/"
                + creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()).doubleValue()
                + "/image.png");
        dto.setType(MerchantApiResponseType.NOTIFY);
        dto.setWalletNumber(account);
        return dto;
    }

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.emptyMap();
    }
}
