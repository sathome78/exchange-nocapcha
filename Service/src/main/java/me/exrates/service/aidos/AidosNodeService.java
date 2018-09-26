package me.exrates.service.aidos;

import org.json.JSONArray;

import java.math.BigDecimal;

public interface AidosNodeService {
    String generateNewAddress();

    BigDecimal getBalance();

    BigDecimal getTransaction(String txId);

    JSONArray getAllTransactions(Integer count, Integer from);

    JSONArray getAllTransactions();
}
