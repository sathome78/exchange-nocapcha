package me.exrates.service.impl;

import info.blockchain.api.APIException;
import info.blockchain.api.receive.ReceiveResponse;
import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.BTCTransaction;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.BlockchainSDKWrapper;
import me.exrates.service.BlockchainService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static java.lang.Integer.*;
import static java.util.Objects.isNull;
import static me.exrates.model.enums.OperationType.INPUT;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */

@Service
@PropertySource("classpath:/merchants/blockchain.properties")
public class BlockchainServiceImpl implements BlockchainService {

    private @Value("${xPub}") String xPub;
    private @Value("${apiCode}") String apiCode;
    private @Value("${callbackUrl}") String callbackUrl;
    private @Value("${secret}") String secret;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PendingPaymentDao pendingPaymentDao;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private BTCTransactionDao btcTransactionDao;

    @Autowired
    private BlockchainSDKWrapper blockchainSDKWrapper;

    private static final Logger LOG = LogManager.getLogger("merchant");
    private static final BigDecimal SATOSHI = new BigDecimal(100_000_000L);
    private static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.CEILING);

    @Override
    @Transactional
    public PendingPayment createPaymentInvoice(final CreditsOperation creditsOperation) {
        final Transaction transaction = transactionService
            .createTransactionRequest(creditsOperation);
        final String transactionHash = computeTransactionHash(transaction);
        final String callback = UriComponentsBuilder
                .fromUriString(callbackUrl)
                .queryParam("invoice_id", transaction.getId())
                .queryParam("secret", transactionHash)
                .build()
                .encode()
                .toString();
        try {
            final ReceiveResponse response = blockchainSDKWrapper.receive(xPub, callback, apiCode);
            final PendingPayment payment = new PendingPayment();
            payment.setTransactionHash(transactionHash);
            payment.setInvoiceId(transaction.getId());
            payment.setAddress(response.getReceivingAddress());
            pendingPaymentDao.create(payment);
            return payment;
        } catch (APIException | IOException e) {
            LOG.error(e);
            throw new RejectedPaymentInvoice();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PendingPayment findByInvoiceId(int invoiceId) {
        return pendingPaymentDao.findByInvoiceId(invoiceId)
            .orElseThrow(()->
                new MerchantInternalException("Invalid invoice_id " + invoiceId));
    }

    @Override
    public Optional<String> notCorresponds(final Map<String, String> pretended, final PendingPayment actual) {
        final String value = pretended.get("value");
        if (isNull(value)) {
            return Optional.of("Amount is invalid");
        }
        if (isNull(pretended.get("address")) ||
            !pretended.get("address").equals(actual.getAddress().get())) {
            return Optional.of("Address is not correct");
        }
        if (isNull(pretended.get("secret")) ||
            !pretended.get("secret").equals(actual.getTransactionHash())) {
            return Optional.of("Secret is invalid");
        }
        if (isNull(pretended.get("transaction_hash"))) {
            return Optional.of("Transaction hash missing");
        }
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String approveBlockchainTransaction(final PendingPayment payment, final Map<String,String> params) {
        if (isNull(params.get("confirmations"))) {
            return "Confirmations not presented";
        }
        final int confirmations = parseInt(params.get("confirmations"));
        final Transaction transaction = transactionService
                .findById(payment.getInvoiceId());
        final BigDecimal targetAmount = transaction.getAmount().add(transaction.getCommissionAmount(), MATH_CONTEXT);
        final BigDecimal currentAmount = new BigDecimal(params.get("value"), MATH_CONTEXT).divide(SATOSHI, MATH_CONTEXT);
        if (targetAmount.compareTo(currentAmount) != 0) {
            if (transaction.getConfirmation() == 0) {
                transactionService.updateTransactionAmount(transaction, currentAmount);
            } else {
                return "Incorrect amount! Amount cannot change since it confirmed at least once";
            }
        }
        transactionService.updateTransactionConfirmation(payment.getInvoiceId(), confirmations);
        if (confirmations < CONFIRMATIONS) {
            return "Waiting for confirmations";
        }
        if (transaction.getOperationType() == INPUT) {
            pendingPaymentDao.delete(payment.getInvoiceId());
        }
        transactionService.provideTransaction(transaction);
        final BigDecimal amount = transaction.getAmount()
            .add(transaction.getCommissionAmount());
        final BTCTransaction btcTransaction = new BTCTransaction();
        btcTransaction.setAmount(amount);
        btcTransaction.setTransactionId(transaction.getId());
        btcTransaction.setHash(params.get("transaction_hash"));
        btcTransactionDao.create(btcTransaction);
        LOG.info("BTC transaction provided " + btcTransaction);
        return "*ok*";
    }

    private String computeTransactionHash(final Transaction request) {
        if (isNull(request) || isNull(request.getCommission()) || isNull(request.getCommissionAmount())) {
            throw new IllegalArgumentException("Argument itself or contain null");
        }
        final String target = new StringJoiner(":")
            .add(String.valueOf(request.getId()))
            .add(secret)
            .toString();
        return algorithmService.sha256(target);
    }
}
