package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.vo.InvoiceData;

import java.util.List;


public interface InvoiceService {

    Transaction createPaymentInvoice(InvoiceData invoiceData);

    boolean provideTransaction(int id, String acceptanceUserEmail);

    List<InvoiceRequest> findAllInvoiceRequests();

    List<InvoiceBank> retrieveBanksForCurrency(Integer currencyId);
}
