package me.exrates.service.apollo;

import org.json.JSONArray;

import java.net.URISyntaxException;

public interface ApolloNodeService {

    JSONArray getTransactions(String address, long lastBlock) throws URISyntaxException;
}
