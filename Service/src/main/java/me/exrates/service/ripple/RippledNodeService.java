package me.exrates.service.ripple;

import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;

/**
 * Created by maks on 05.05.2017.
 */
public interface RippledNodeService {

    void signTransaction(RippleTransaction transaction);

    void submitTransaction(RippleTransaction transaction);

    boolean checkSendedTransactionConsensus(String txHash);

    RippleAccount porposeAccount();
}
