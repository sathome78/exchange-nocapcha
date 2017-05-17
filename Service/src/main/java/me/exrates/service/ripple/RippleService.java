package me.exrates.service.ripple;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IMerchantService;

/**
 * Created by maks on 11.05.2017.
 */
public interface RippleService extends IMerchantService {

    /*method for admin manual check transaction by hash*/
    void manualCheckNotReceivedTransaction(String hash);

    /*return: true if tx validated; false if not validated but validationin process,
        throws Exception if declined*/
    boolean checkSendedTransaction(String hash);
}
