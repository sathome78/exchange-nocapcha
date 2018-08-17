package me.exrates.service.tron;

public interface TronTransactionsService {
    boolean checkIsTransactionConfirmed(String txHash);
}
