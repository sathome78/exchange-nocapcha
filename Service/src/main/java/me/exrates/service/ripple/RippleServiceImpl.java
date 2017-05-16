package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import me.exrates.service.exception.WithdrawRequestPostException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2
@Service
public class RippleServiceImpl implements RippleService {

    @Autowired
    private RippleTransactionService rippleTransactionService;
    @Autowired
    private RippledNodeService rippledNodeService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;


    public void onTransactionReceive(JSONObject result) {
        String account = result.getString("account");
        /*getting account from db*/
        RippleAccount rippleAccount = new RippleAccount();
        BigDecimal amount = result.getBigDecimal("amount");


    }

    @Override
    public String withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        if (!"XRP".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        return rippleTransactionService.withdraw(withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) throws RefillRequestIdNeededException {
        RippleAccount account = rippledNodeService.porposeAccount();
        String message = messageSource.getMessage("merchants.refill.edr",
                new Object[]{account.getName()}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", account.getName());
            put("message", message);
            put("qr", account.getName());
            put("privKey", account.getSecret());
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String merchantTransactionId = params.get("id");
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName("XRP");
        Merchant merchant = merchantService.findByName("XRP");

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("amount")));
        BigDecimal accountBalance = rippleTransactionService.getAccountBalance(address);
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(StringUtils.isEmpty(merchantTransactionId) ? hash : merchantTransactionId)
                .build();
        if(merchant.isToMainAccountTransferringNeeded()) {
            requestAcceptDto.setToMainAccountTransferringNeeded(true);
        }
        try {
            if (accountBalance.compareTo(amount) <= 0) {
                requestAcceptDto.setRemark(messageSource.getMessage("merchant.ripple.emptyRefill",
                        new String[]{amount.toString()}, Locale.ENGLISH));
                refillService.autoAcceptRefillEmptyRequest(requestAcceptDto);
            } else {
                refillService.autoAcceptRefillRequest(requestAcceptDto);
            }
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

}
