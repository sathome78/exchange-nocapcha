package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.service.BitcoinService;
import me.exrates.service.MerchantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("BitcoinPaymentService")
public class BitcoinPaymentService implements MerchantPaymentService {
    private static final Logger LOGGER = LogManager.getLogger("merchant");

    @Autowired
    @Qualifier("bitcoinServiceImpl")
    private BitcoinService bitcoinService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MessageSource messageSource;

    /*@Override
    @Transactional
    public MerchantInputResponseDto preparePayment(String email, Payment payment, Locale locale) {
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);

        MerchantInputResponseDto dto = new MerchantInputResponseDto();
        dto.setType(MerchantApiResponseType.NOTIFY);
        final PendingPayment pendingPayment = bitcoinService.createInvoice(creditsOperation);
        final String address = Optional.ofNullable(pendingPayment
            .getAddress()).orElseThrow(() ->new MerchantInternalException("Address not presented"));
        final String notification = merchantService
                    .sendDepositNotification(address,email ,locale, creditsOperation, "merchants.depositNotification.body");
        dto.setData(notification);
        dto.setWalletNumber(address);
        dto.setQr("bitcoin:" + address + "?amount="
                + creditsOperation.getAmount().doubleValue() + "&message=Donation%20for%20project%20Exrates");
        return dto;

    }*/

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}