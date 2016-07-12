package me.exrates.service.impl;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.InvoiceService;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private TransactionService transactionService;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @Override
    @Transactional
    public Transaction createPaymentInvoice(final CreditsOperation creditsOperation) {

        final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);

        return transaction;
    }

    @Override
    @Transactional
    public boolean provideTransaction(int id) {

        Transaction transaction = transactionService.findById(id);
        try {
            transactionService.provideTransaction(transaction);
        }catch (Exception e){
            LOG.error(e);
            return false;
        }
       return true;
    }

}
