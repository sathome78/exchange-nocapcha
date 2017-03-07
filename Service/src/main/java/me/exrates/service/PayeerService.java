package me.exrates.service;

import me.exrates.model.CreditsOperation;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface PayeerService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String, String> params);

}
