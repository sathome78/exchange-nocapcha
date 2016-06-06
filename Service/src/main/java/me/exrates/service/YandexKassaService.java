package me.exrates.service;

import me.exrates.model.CreditsOperation;

import java.util.Map;

public interface YandexKassaService {

    /**
     * This method of prepearing parameters for the payment form
     * @param creditsOperation
     * @param email
     * @return Map with parameters
     */
    Map<String, String> preparePayment(CreditsOperation creditsOperation, String email);

    /**
     * Confirms payment in DB
     * @param params
     * @return true if checks accepted, false if none
     */
    boolean confirmPayment(final Map<String,String> params);

}
