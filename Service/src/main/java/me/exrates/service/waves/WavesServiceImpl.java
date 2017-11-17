package me.exrates.service.waves;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.waves.WavesTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.ParamMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("wavesServiceImpl")
public class WavesServiceImpl implements WavesService {

    @Autowired
    private WavesRestClient restClient;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MessageSource messageSource;

    private final String currencyName = "WAVES";
    private final String merchantName = "Waves";

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = restClient.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("message", message);
            put("address", address);
            put("qr", address);
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Optional<String> refillRequestIdResult = Optional.ofNullable(params.get("requestId"));
        Integer currencyId = Integer.parseInt(ParamMapUtils.getIfNotNull(params, "currencyId"));
        Integer merchantId = Integer.parseInt(ParamMapUtils.getIfNotNull(params, "merchantId"));
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        WavesTransaction wavesTransaction = restClient.getTransactionById(txId);





    }

    private void processWavesTransaction(WavesTransaction wavesTransaction) {


    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    public void processWavesTransactionsForKnownAddresses() {
        Currency currency = currencyService.findByName(currencyName);
        Merchant merchant = merchantService.findByName(merchantName);

        refillService.findAllAddresses(merchant.getId(), currency.getId()).stream()
                .flatMap(address -> restClient.getTransactionsForAddress(address).stream().filter(transaction -> address.equals(transaction.getRecipient())))
                .forEach(transaction -> {


                });
    }

}
