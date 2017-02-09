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
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


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
        InvoiceBank invoiceBank = new InvoiceBank();
        invoiceBank.setId(invoiceData.getBankId());
        invoiceRequest.setInvoiceBank(invoiceBank);
        invoiceRequest.setUserFullName(invoiceData.getUserFullName());
        invoiceRequest.setRemark(StringEscapeUtils.escapeHtml(invoiceData.getRemark()));
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
    public List<InvoiceBank> findBanksForCurrency(Integer currencyId) {
        return invoiceRequestDao.findInvoiceBanksByCurrency(currencyId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceBank findBankById(Integer bankId) {
        return invoiceRequestDao.findBankById(bankId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceRequest> findRequestById(Integer transactionId) {
        return invoiceRequestDao.findById(transactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceRequest> findUnconfirmedRequestById(Integer transactionId) {
        return invoiceRequestDao.findByIdAndNotConfirmed(transactionId);
    }

    @Override
    @Transactional
    public void updateConfirmationInfo(InvoiceRequest invoiceRequest) {
        invoiceRequestDao.updateConfirmationInfo(invoiceRequest);
    }

    @Override
    public List<InvoiceRequest> findAllRequestsForUser(String userEmail) {
        return invoiceRequestDao.findAllForUser(userEmail);
    }
}
