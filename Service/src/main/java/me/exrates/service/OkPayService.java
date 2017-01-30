package me.exrates.service;

import me.exrates.model.CreditsOperation;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface OkPayService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    public boolean confirmPayment(Map<String,String> params);
}
