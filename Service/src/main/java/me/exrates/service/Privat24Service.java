package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IMerchantService;

import java.util.Map;

public interface Privat24Service extends IMerchantService {

    Map<String, String> preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String, String> params, String signature, String payment);

}
