package me.exrates.dao;

import me.exrates.model.User;

public interface EDCMerchantDao {

    void createAddress(String address, User user);

    boolean checkMerchantTransactionIdIsEmpty(String merchantTransactionId);

    String findUserEmailByAddress(String address);

    void createMerchantTransaction(String address, String merchantTransactionId, Integer transactionId);

}
