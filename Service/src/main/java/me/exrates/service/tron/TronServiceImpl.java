package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Log4j2
@Service
public class TronServiceImpl implements TronService {

    @Autowired
    private TronNodeService tronNodeService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MessageSource messageSource;


    private final static String CURRENCY_NAME = "TRX";
    private final static String MERCHANT_NAME = "TRX";
    private int merchantId;
    private int currencyId;

    private Set<String> addressesHEX = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Set<String> getAddressesHEX() {
        return addressesHEX;
    }

    @PostConstruct
    private void init() {
        /*get hex addresses
        * tron addresses - pub_key field*/
        /*addresses.addAll();*/
        merchantId = merchantService.findByName(MERCHANT_NAME).getId();
        currencyId = currencyService.findByName(CURRENCY_NAME).getId();
    }

    public void checkConfirmationsAndProceed() {

    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        TronNewAddressDto dto = tronNodeService.getNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{dto.getAddress()}, request.getLocale());
        addressesHEX.add(dto.getHexAddress());
        return new HashMap<String, String>() {{
            put("address",  dto.getAddress());
            put("privKey", dto.getPrivateKey());
            put("pubKey", dto.getHexAddress());
            put("message", message);
            put("qr", dto.getAddress());
        }};
    }

    @Override
    public RefillRequestAcceptDto createRequest(TronReceivedTransactionDto dto) {
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(dto.getAddressBase58())
                .merchantId(merchantId)
                .currencyId(currencyId)
                .amount(new BigDecimal(dto.getAmount()))
                .merchantTransactionId(dto.getHash())
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        return requestAcceptDto;
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {
        try {
            refillService.putOnBchExamRefillRequest(
                    RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestAcceptDto.getRequestId())
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(requestAcceptDto.getAddress())
                            .amount(requestAcceptDto.getAmount())
                            .hash(requestAcceptDto.getMerchantTransactionId())
                            .build());
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(CURRENCY_NAME);
        Merchant merchant = merchantService.findByName(MERCHANT_NAME);
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Tron withdraw method not implemented!");
    }

    @Override
    public int getMerchantId() {
        return merchantId;
    }

    @Override
    public int getCurrencyId() {
        return currencyId;
    }
}
