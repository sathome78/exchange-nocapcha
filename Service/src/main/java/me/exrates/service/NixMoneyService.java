package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface NixMoneyService extends IMerchantService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String,String> params);

    void invalidateTransaction(Transaction transaction);

}
