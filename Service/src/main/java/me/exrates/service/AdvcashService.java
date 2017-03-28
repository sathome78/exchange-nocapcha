package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Service
public interface AdvcashService extends IMerchantService {

    Map<String, String> getAdvcashParams(Transaction transaction);

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation);

    void provideTransaction(Transaction transaction);

    void invalidateTransaction(Transaction transaction);

    boolean checkHashTransactionByTransactionId(int invoiceId, String inputHash);

}
