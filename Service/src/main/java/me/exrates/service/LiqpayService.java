package me.exrates.service;


import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface LiqpayService {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    Map<String,Object> getResponse(String data);

    boolean checkHashTransactionByTransactionId(int invoiceId, String inputHash);

    void provideTransaction(Transaction transaction);

}
