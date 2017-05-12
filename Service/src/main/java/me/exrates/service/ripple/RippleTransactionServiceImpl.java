package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.RippleTransactionDao;
import me.exrates.model.Transaction;
import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import me.exrates.model.enums.RippleTransactionStatus;
import me.exrates.model.enums.RippleTransactionType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/ripple.properties")
public class RippleTransactionServiceImpl implements RippleTransactionService {

    @Autowired
    private RippledNodeService rippledNodeService;
    @Autowired
    private RippleTransactionDao transactionDao;
    @Autowired
    private TransactionService transactionService;


    private @Value("${ripple.account.address}") String address;
    private @Value("${ripple.account.secret}") String secret;

    private static final Integer XRP_AMOUNT_MULTIPLIER = 1000000;
    private static final Integer XRP_DECIMALS = 6;


    @PostConstruct
    public void init() {
        /*check all transactions with status 'submitted' for validation*/
        /*check all signed transactions and submit them*/
    }

    protected void processIncomeTransaction() {
        transferToMainAccount();
    }


    private void transferToMainAccount() {

    }

    /*send xrp*/
    @Transactional
    protected void sendMoney(RippleAccount account, BigDecimal amount, String destinationAccount, RippleTransactionType type) {
        RippleTransaction transaction = prepareTransaction(amount, account, destinationAccount);
        transaction.setUserId(account.getUser().getId());
        transaction.setType(type);
        rippledNodeService.signTransaction(transaction);
        transaction.setStatus(RippleTransactionStatus.SIGNED);
        reserveCosts();
        transactionDao.createRippleTransaction(transaction);
        rippledNodeService.submitTransaction(transaction);
        transaction.setStatus(RippleTransactionStatus.SUBMITTED);
        transactionDao.updateRippleTransaction(transaction);
        try {
            Thread.currentThread().wait(1500);
        } catch (InterruptedException e) {
            log.error("error thread waiting {}", e);
        }
        boolean verified = rippledNodeService.checkSendedTransactionConsensus(transaction.getTxHash());
        if (verified) {
            transaction.setStatus(RippleTransactionStatus.CONFIRMED);
            provideTransactionAndTransferFunds(transaction.getTransactionId());

            transactionDao.updateRippleTransaction(transaction);
        }
    }

    /*for refill transactions*/
    @Override
    private void provideTransactionAndTransferFunds(int txId){
        Transaction transaction = transactionService.findById(txId);
        if (!transaction.isProvided()){
            transactionService.provideTransaction(transaction);
            log.debug("Ripple transaction " + transaction.toString() + " --- PROVIDED!!!");
            transferToMainAccount(ethereumNodeDao.findAddressByMerchantTransactionId(merchantTransactionId), transaction.getAmount());
        }
    }

    private RippleTransaction prepareTransaction(BigDecimal amount, RippleAccount account, String destinationAccount) {
        return RippleTransaction.builder()
                .amount(amount)
                .sendAmount(normalizeAmountToString(amount))
                .destinationAddress(destinationAccount)
                .issuerAddress(account.getName())
                .issuerSecret(account.getSecret())
                .build();
    }

    private String normalizeAmountToString(BigDecimal amount) {

        return amount
                .setScale(XRP_DECIMALS, RoundingMode.HALF_DOWN)
                .multiply(new BigDecimal(XRP_AMOUNT_MULTIPLIER))
                .toBigInteger()
                .toString();
    }

    private void reserveCosts() {

    }
}
