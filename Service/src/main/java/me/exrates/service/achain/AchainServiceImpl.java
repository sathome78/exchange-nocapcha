package me.exrates.service.achain;

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
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2(topic = "achain")
@Service
public class AchainServiceImpl implements AchainService {

    private final NodeService nodeService;
    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final RefillService refillService;

    @Autowired
    public AchainServiceImpl(NodeService nodeService, CurrencyService currencyService, MerchantService merchantService, RefillService refillService) {
        this.nodeService = nodeService;
        this.currencyService = currencyService;
        this.merchantService = merchantService;
        this.refillService = refillService;
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        /*autowithdraw not implemented*/
        throw new RuntimeException("autowithdraw not supported");
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return new HashMap<String, String>() {{
            put("address",  generateRandomSymbolsAndAddToAddress(nodeService.getMainAccountAddress()));
           /* put("message", message);*/
        }};
    }

    private String generateRandomSymbolsAndAddToAddress(String mainAddress) {
        String generatedString = RandomStringUtils.randomAlphanumeric(32);
        return mainAddress.concat(generatedString);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName(params.get("merchant"));
        BigDecimal amount = new BigDecimal(params.get("amount"));
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.warn("achain tx duplicated {}", hash);
            return;
        }
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

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }
}
