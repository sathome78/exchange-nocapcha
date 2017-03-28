package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface OkPayService extends IMerchantService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    public boolean confirmPayment(Map<String,String> params);
}
