package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.vo.InvoiceData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface InvoiceService {

    Transaction createPaymentInvoice(InvoiceData invoiceData);

    boolean provideTransaction(int id, String acceptanceUserEmail);

    List<InvoiceRequest> findAllInvoiceRequests();

    List<InvoiceBank> retrieveBanksForCurrency(Integer currencyId);

    Optional<InvoiceRequest> findRequestById(Integer transactionId);

    @Transactional(readOnly = true)
    Optional<InvoiceRequest> findUnconfirmedRequestById(Integer transactionId);

    @Transactional
    void updateConfirmationInfo(InvoiceRequest invoiceRequest);
}
