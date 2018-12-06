package me.exrates.service.qiwi;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "Qiwi")
@Service
@PropertySource("classpath:/merchants/qiwi.properties")
public class QiwiServiceImpl implements QiwiService {

    @Value("${qiwi.account.address}")
    private String mainAddress;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MessageSource messageSource;


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = genrateDefaultMemo(request.getUserId());/*mock impl*/
        String message = messageSource.getMessage("merchants.refill.qiwi",
                new Object[]{mainAddress, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address",  destinationTag);
            put("message", message);
            put("qr", mainAddress);
        }};
    }

    private String genrateDefaultMemo(int userId) {
       return CryptoUtils.generateDestinationTag(userId, 12);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName(params.get("merchant"));
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented for qiwi");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return !address.equals(mainAddress);
    }
}
