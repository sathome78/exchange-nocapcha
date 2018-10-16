package me.exrates.service.apollo;

public interface ApolloNodeService {

    String getTransactions(String address, int firstIndex, int lastIndex);

    String getTransaction(String txHash);
}
