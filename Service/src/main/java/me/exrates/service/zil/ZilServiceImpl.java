package me.exrates.service.zil;

import lombok.Synchronized;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

//TODO add log
@Service
@Conditional(MonolitConditional.class)
public class ZilServiceImpl implements ZilService{

    private static final String CURRENCY_NAME = "ZIL";
    private Merchant merchant;
    private Currency currency;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private ZilCurrencyService zilCurrencyService;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(CURRENCY_NAME);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String privKey = zilCurrencyService.generatePrivateKey();
        String address = zilCurrencyService.getAddressFromPrivateKey(privKey);
        String message = messageSource.getMessage("merchants.refill.xlm",
                new Object[] {address}, request.getLocale());
        return new HashMap<String, String>(){{
            put("privKey", privKey);
            put("pubKey", zilCurrencyService.getPublicKeyFromPrivateKey(privKey));
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        //scaled amount
        BigDecimal amount = new BigDecimal(params.get("amount"));
//        long fee = params.get

        if (checkTransactionForDuplicate(hash)) {
//            log.warn("*** zil *** transaction {} already accepted", hash);
            return;
        }

        try {
            zilCurrencyService.createTransaction(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        refillService.createAndAutoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

    private boolean checkTransactionForDuplicate(String hash){
        return StringUtils.isEmpty(hash) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(), hash).isPresent();
    }
}
