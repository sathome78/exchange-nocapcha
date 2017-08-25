package me.exrates.service.lisk;

import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;

import java.util.List;

public interface LiskRestClient {
    LiskTransaction getTransactionById(String txId);

    List<LiskTransaction> getTransactionsByRecipient(String recipientAddress);

    String sendTransaction(LiskSendTxDto dto);

    LiskAccount createAccount(String secret);

    LiskAccount getAccountByAddress(String address);
}
