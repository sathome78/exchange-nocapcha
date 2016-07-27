package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;

import java.util.List;


public interface InvoiceService {

    Transaction createPaymentInvoice(CreditsOperation creditsOperation);

    boolean provideTransaction(int id, String acceptanceUserEmail);

    List<InvoiceRequest> findAllInvoiceRequests();

}
