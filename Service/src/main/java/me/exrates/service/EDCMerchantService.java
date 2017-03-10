package me.exrates.service;

import me.exrates.model.CreditsOperation;

import java.util.Map;

/**
 * Created by ajet on 06.03.2017.
 */
public interface EDCMerchantService {
    String createAddress(CreditsOperation creditsOperation) throws Exception;

    boolean checkMerchantTransactionIdIsEmpty(String merchantTransactionId);

    void createAndProvideTransaction(Map<String,String> params);
}
