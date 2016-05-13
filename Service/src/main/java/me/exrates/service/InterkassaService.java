package me.exrates.service;

import me.exrates.model.CreditsOperation;

import java.util.Map;

public interface InterkassaService {

    Map<String, String> preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String, String> params);

}
