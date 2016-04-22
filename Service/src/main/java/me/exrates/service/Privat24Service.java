package me.exrates.service;

import me.exrates.model.CreditsOperation;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface Privat24Service {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String, String> params, String signature, String payment);

}
