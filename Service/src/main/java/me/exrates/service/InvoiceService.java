package me.exrates.service;

import me.exrates.model.ClientBank;
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

    List<InvoiceBank> findBanksForCurrency(Integer currencyId);

    @Transactional(readOnly = true)
    List<ClientBank> findClientBanksForCurrency(Integer currencyId);

    @Transactional(readOnly = true)
    InvoiceBank findBankById(Integer bankId);

    Optional<InvoiceRequest> findRequestById(Integer transactionId);

    @Transactional(readOnly = true)
    Optional<InvoiceRequest> findUnconfirmedRequestById(Integer transactionId);

    @Transactional
    void updateConfirmationInfo(InvoiceRequest invoiceRequest);

    void updateReceiptScan(Integer invoiceId, String receiptScanPath);

    List<InvoiceRequest> findAllRequestsForUser(String userEmail);
}
