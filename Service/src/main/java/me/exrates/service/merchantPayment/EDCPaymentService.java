package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.service.EDCService;
import me.exrates.service.MerchantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("EDCPaymentService")
public class EDCPaymentService implements MerchantPaymentService {
    private static final Logger LOGGER = LogManager.getLogger("merchant");

    @Autowired
    private EDCService edcService;
    
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
        final String account;
        try {
  //          account = edcService.createInvoice(creditsOperation);
            account = edcMerchantService.createAddress(creditsOperation);
            LOGGER.debug(account);

            final String notification = merchantService
                .sendDepositNotification(account,email ,locale, creditsOperation, "merchants.depositNotification.body");
            dto.setData(notification);
            dto.setWalletNumber(account);
            return dto;
        } catch (Exception e) {
            final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale);
            LOGGER.error(e);
            throw new MerchantInternalException(error);
        }
    }*/

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
