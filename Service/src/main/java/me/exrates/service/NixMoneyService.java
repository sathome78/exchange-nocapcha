package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface NixMoneyService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String,String> params);

    void invalidateTransaction(Transaction transaction);

}
