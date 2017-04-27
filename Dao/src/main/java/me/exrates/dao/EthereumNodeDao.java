package me.exrates.dao;

import me.exrates.model.EthereumAccount;

import java.util.List;
import java.util.Optional;

public interface EthereumNodeDao {

    void createAddress(EthereumAccount ethereumAccount);

    Optional<EthereumAccount> findByAddress(String address);

    List<String> findAllAddresses();

    String findUserEmailByAddress(String address);

    void createMerchantTransaction(String address, String merchantTransactionId, Integer transactionId);

    List<String> findPendingTransactions();

    Integer findTransactionId(String merchantTransactionId);

    boolean isMerchantTransactionExists(String merchantTransactionId);

    String findAddressByMerchantTransactionId(String merchantTransactionId);
}
