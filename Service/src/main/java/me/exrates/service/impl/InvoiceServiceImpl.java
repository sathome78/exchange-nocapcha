package me.exrates.service.impl;

import me.exrates.dao.InvoiceRequestDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.InvoiceService;
import me.exrates.service.NotificationService;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InvoiceRequestDao invoiceRequestDao;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @Override
    @Transactional
    public Transaction createPaymentInvoice(final InvoiceData invoiceData) {
        CreditsOperation creditsOperation = invoiceData.getCreditsOperation();
        final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        invoiceRequest.setTransaction(transaction);
        invoiceRequest.setUserEmail(creditsOperation.getUser().getEmail());
        invoiceRequest.setBankId(invoiceData.getBankId());
        invoiceRequest.setUserAccount(invoiceData.getUserAccount());
        invoiceRequest.setRemark(invoiceData.getRemark());
        invoiceRequestDao.create(invoiceRequest, creditsOperation.getUser());
        return transaction;
    }

    @Override
    @Transactional
    public boolean provideTransaction(int id, String acceptanceUserEmail) {

        Transaction transaction = transactionService.findById(id);
        try {
            transactionService.provideTransaction(transaction);
            InvoiceRequest invoiceRequest = invoiceRequestDao.findById(id).get();
            invoiceRequest.setAcceptanceUserEmail(acceptanceUserEmail);
            invoiceRequestDao.setAcceptance(invoiceRequest);
            notificationService.notifyUser(invoiceRequest.getUserId(), NotificationEvent.IN_OUT, "paymentRequest.accepted.title",
                    "paymentRequest.accepted.message", null);
        }catch (Exception e){
            LOG.error(e);
            return false;
        }
       return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceRequest> findAllInvoiceRequests() {
        return invoiceRequestDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceBank> retrieveBanksForCurrency(Integer currencyId) {
        return invoiceRequestDao.findInvoiceBanksByCurrency(currencyId);
    }
}
