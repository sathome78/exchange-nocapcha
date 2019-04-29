package me.exrates.service;

import me.exrates.service.impl.GapiCurrencyServiceImpl.Transaction;

import java.util.List;

public interface GapiCurrencyService {

    String generateNewAddress();

    List<Transaction> getAccountTransactions();
}
