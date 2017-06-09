package me.exrates.service.stellar;

import me.exrates.service.merchantStrategy.IMerchantService;
import org.json.JSONObject;
import org.stellar.sdk.responses.TransactionResponse;

/**
 * Created by maks on 06.06.2017.
 */
public interface StellarService extends IMerchantService {

    /*method for admin manual check transaction by hash*/
    void manualCheckNotReceivedTransaction(String hash);

    /*return: true if tx validated; false if not validated but validationin process,
        throws Exception if declined*/
    boolean checkSendedTransaction(String hash, String additionalParams);


    @Override
    default Boolean createdRefillRequestRecordNeeded() {
        return false;
    }

    @Override
    default Boolean needToCreateRefillRequestRecord() {
        return false;
    }

    @Override
    default Boolean toMainAccountTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean generatingAdditionalRefillAddressAvailable() {
        return false;
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return true;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    void onTransactionReceive(TransactionResponse payment, String amount);
}
