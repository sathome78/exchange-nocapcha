package me.exrates.service.ripple;

import me.exrates.model.CreditsOperation;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import me.exrates.service.exception.WithdrawRequestPostException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

/**
 * Created by maks on 11.05.2017.
 */
@Service
public class RippleServiceImpl implements RippleService {

    @Autowired
    private RippleTransactionService rippleTransactionService;
    @Autowired
    private RippledNodeService rippledNodeService;

    public void onTransactionReceive(JSONObject result) {
        String account = result.getString("account");
        /*getting account from db*/
        RippleAccount rippleAccount = new RippleAccount();
        BigDecimal amount = result.getBigDecimal("amount");


    }

    @Override
    public String createAddress(CreditsOperation creditsOperation) {
        RippleAccount account = rippledNodeService.porposeAccount();
        account.setUser(creditsOperation.getUser());
        /*persist data here*/
        return account.getName();
    }

    @Override
    public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        if (!"XRP".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        rippleTransactionService.withdraw(withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) throws RefillRequestIdNeededException {
        RippleAccount account = rippledNodeService.porposeAccount();

        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public String generateFullUrl(String url, Properties properties) {
        return null;
    }
}
