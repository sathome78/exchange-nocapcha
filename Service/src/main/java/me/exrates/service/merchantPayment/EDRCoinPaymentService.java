package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.service.EDRCService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("EDRCoinPaymentService")
public class EDRCoinPaymentService implements MerchantPaymentService {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private EDRCService edrcService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LogManager.getLogger("merchant");

    @Override
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);
        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setType(MerchantApiResponseType.NOTIFY);
        final PendingPayment pendingPayment = edrcService
                .createPaymentInvoice(creditsOperation);
        final String address = Optional.ofNullable(pendingPayment
                .getAddress()).orElseThrow(() -> new MerchantInternalException("Address not presented"));
        final String notification = merchantService
                .sendDepositNotification(address,
                        email , locale, creditsOperation, "merchants.depositNotification.body");
        dto.setData(notification);
        dto.setWalletNumber(address);
        return dto;
    }

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
