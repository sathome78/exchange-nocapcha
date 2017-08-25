package me.exrates.service.lisk;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.LiskCreateAddressException;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.ParamMapUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

@Service
public class LiskServiceImpl implements LiskService {

    @Autowired
    private RefillService refillService;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private LiskRestClient liskRestClient;

    @Autowired
    private MessageSource messageSource;

    private final String merchantName = "Lisk";
    private final String currencyName = "LSK";
    private final Integer MIN_CONFIRMATIONS = 10;


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        try {
            String secret = String.join(" ", MnemonicCode.INSTANCE.toMnemonic(SecureRandom.getSeed(16)));
            LiskAccount account = createNewLiskAccount(secret);
            String address = account.getAddress();

            String message = messageSource.getMessage("merchants.refill.btc",
                    new Object[]{address}, request.getLocale());
            Map<String, String> result = new HashMap<String, String>() {{
               put("message", message);
               put("address", address);
               put("pubKey", account.getPublicKey());
               put("brainPrivKey", secret);
               put("qr", address);
            }};
            return result;
        } catch (MnemonicException.MnemonicLengthException e) {
            throw new LiskCreateAddressException(e);
        }



    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Currency currency = currencyService.findByName(currencyName);
        Merchant merchant = merchantService.findByName(merchantName);
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");

        LiskTransaction transaction = getTransactionById(txId);

        Optional<Integer> refillRequestIdResult = refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(address,
                merchant.getId(), currency.getId(), txId);
        if (!refillRequestIdResult.isPresent()) {


        }



    }


    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return Collections.emptyMap();
    }

    @Override
    public LiskTransaction getTransactionById(String txId) {
        return liskRestClient.getTransactionById(txId);
    }

    @Override
    public List<LiskTransaction> getTransactionsByRecipient(String recipientAddress) {
        return liskRestClient.getTransactionsByRecipient(recipientAddress);
    }

    @Override
    public String sendTransaction(String secret, BigDecimal amount, String recipientId) {
        LiskSendTxDto dto = new LiskSendTxDto();
        dto.setSecret(secret);
        dto.setAmount(amount);
        dto.setRecipientId(recipientId);
        return liskRestClient.sendTransaction(dto);
    }

    @Override
    public LiskAccount createNewLiskAccount(String secret) {
       return liskRestClient.createAccount(secret);
    }

    @Override
    public LiskAccount getAccountByAddress(String address) {
        return liskRestClient.getAccountByAddress(address);
    }

}
