package me.exrates.service.waves;

import me.exrates.model.dto.merchants.waves.WavesPayment;
import me.exrates.model.dto.merchants.waves.WavesTransaction;

import java.util.List;

public interface WavesRestClient {
    String generateNewAddress();

    String transferCosts(WavesPayment payment);

    List<WavesTransaction> getTransactionsForAddress(String address);

    WavesTransaction getTransactionById(String id);
}
