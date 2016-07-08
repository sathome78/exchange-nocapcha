package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;


public interface InvoiceService {

    Transaction createPaymentInvoice(CreditsOperation creditsOperation);

    boolean provideTransaction(int id);

}
