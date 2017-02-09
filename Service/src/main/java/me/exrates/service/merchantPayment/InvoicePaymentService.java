package me.exrates.service.merchantPayment;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Created by OLEG on 05.09.2016.
 */
@Component("InvoicePaymentService")
@PropertySource("classpath:/merchants/invoice.properties")
public class InvoicePaymentService implements MerchantPaymentService {
    private @Value("${invoice.accountCNY}") String accountCNY;
    private @Value("${invoice.accountIDR}") String accountIDR;
    private @Value("${invoice.accountTHB}") String accountTHB;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InvoiceService invoiceService;

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
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setCreditsOperation(creditsOperation);
        final Transaction transaction = invoiceService.createPaymentInvoice(invoiceData);
        final String notification = merchantService
                .sendDepositNotification("",
                        email , locale, creditsOperation, "merchants.depositNotificationWithCurrency" +
                                creditsOperation.getCurrency().getName() +
                                ".old");
        dto.setData(notification);
        switch (creditsOperation.getCurrency().getName()) {
            case "CNY":
                dto.setWalletNumber(accountCNY);
                break;
            case "IDR":
                dto.setWalletNumber(accountIDR);
                break;
            case "THB":
                dto.setWalletNumber(accountTHB);
                break;
            default:
                dto.setWalletNumber("");
                break;
        }
        return dto;

    }

    @Override
    public Map<String, String> preparePostPayment(String email, CreditsOperation creditsOperation, Locale locale) {
        return Collections.EMPTY_MAP;
    }
}
