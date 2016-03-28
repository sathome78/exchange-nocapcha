package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Service
public interface AdvcashService {

    Map<String, String> getPerfectMoneyParams(Transaction transaction);

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation);

    void provideTransaction(Transaction transaction);

    void invalidateTransaction(Transaction transaction);

}
