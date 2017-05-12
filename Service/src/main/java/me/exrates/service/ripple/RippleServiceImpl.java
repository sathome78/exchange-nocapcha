package me.exrates.service.ripple;

import me.exrates.model.CreditsOperation;
import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by maks on 11.05.2017.
 */
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
        rippleTransactionService.


    }

    @Override
    public String createAddress(CreditsOperation creditsOperation) {
        RippleAccount account = rippledNodeService.porposeAccount();
        account.setUser(creditsOperation.getUser());
        /*persist data here*/
        return account.getName();
    }
}
