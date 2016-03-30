package me.exrates.service.impl;

import info.blockchain.api.APIException;
import info.blockchain.api.receive.Receive;
import info.blockchain.api.receive.ReceiveResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.BTCTransaction;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.AlgorithmService;
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

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/${spring.profile.active}/merchants/blockchain.properties")
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

    private static final BigDecimal SATOSHI = BigDecimal.valueOf(100000000L);
    private static final MathContext MATH_CONTEXT =
        new MathContext(9, RoundingMode.CEILING);

    private static final int CONFIRMATIONS = 4;

    private static final Logger logger = LogManager.getLogger("merchant");

    @Override
    @Transactional
    public PendingPayment createPaymentInvoice(final CreditsOperation creditsOperation) {
        final Transaction transaction = transactionService
            .createTransactionRequest(creditsOperation);
        final String transactionHash = computeTransactionHash(transaction);
        final String callback = UriComponentsBuilder
                .fromUriString(callbackUrl)
                .queryParam("invoice_id", transaction.getId())
                .queryParam("secret",transactionHash)
                .build()
                .encode()
                .toString();
        logger.debug(callback);
        try {
            final ReceiveResponse response = Receive
                .receive(xPub, callback, apiCode);
            final PendingPayment payment = new PendingPayment();
            payment.setTransactionHash(transactionHash);
            payment.setInvoiceId(transaction.getId());
            payment.setAddress(response.getReceivingAddress());
            pendingPaymentDao.create(payment);
            return payment;
        } catch (APIException | IOException e) {
            logger.error(e);
            throw new RejectedPaymentInvoice();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PendingPayment findByInvoiceId(int invoiceId) {
        return pendingPaymentDao.findByInvoiceId(invoiceId)
            .orElseThrow(()->
                new MerchantInternalException("Invalid invoice_id "+invoiceId));
    }

    @Override
    public Optional<String> notCorresponds(final Map<String, String> pretended,
        final PendingPayment actual) {
        final String value = pretended.get("value");
        if (Objects.isNull(value)) {
            return Optional.of("Amount is invalid");
        }
        if (Objects.isNull(pretended.get("address")) ||
            !pretended.get("address").equals(
                actual.getAddress()
                .orElseThrow(()->
                    new MerchantInternalException("Address is not presented"))
            )) {
            return Optional.of("Address is not correct");
        }
        if (Objects.isNull(pretended.get("secret")) ||
            !pretended.get("secret").equals(actual.getTransactionHash())) {
            return Optional.of("Secret is invalid");
        }
        if (Objects.isNull(pretended.get("transaction_hash"))) {
            return Optional.of("Transaction hash missing");
        }
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String approveBlockchainTransaction(final PendingPayment payment,
        final Map<String,String> params) {
        if (Objects.isNull(params.get("confirmations")) ||
            Integer.valueOf(params.get("confirmations")) < CONFIRMATIONS) {
            return "Waiting for confirmations";
        }
        final Transaction transaction = transactionService
            .findById(payment.getInvoiceId());
        if (transaction.getOperationType()== OperationType.INPUT){
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
        logger.info("BTC transaction provided "+btcTransaction);
        return "*ok*";
    }

    @Override
    @Transactional
    public void provideOutputPayment(Payment payment, CreditsOperation creditsOperation) {
//        final BigDecimal amount = creditsOperation.getAmount().add(creditsOperation.getCommissionAmount());
//        final Transaction transactionRequest = transactionService.createTransactionRequest(creditsOperation);
//        transactionService.provideTransaction(transactionRequest);
//        final String txHash = sendBTC(payment.getDestination(), amount)
//                .orElseThrow(RuntimeException::new);
//        final BTCTransaction btcTransaction = new BTCTransaction();
//        btcTransaction.setTransactionId(transactionRequest.getId());
//        btcTransaction.setAmount(amount);
//        btcTransaction.setHash(txHash);
//        persistBlockchainTransaction(null, btcTransaction);
        throw new UnsupportedOperationException();
    }


    private Optional<String> sendBTC(String address,BigDecimal amount) {
        throw new UnsupportedOperationException();
    }

    protected String computeTransactionHash(final Transaction request) {
        final String target = new StringJoiner(":")
            .add(String.valueOf(request.getId()))
            .add(request
                .getAmount()
                .stripTrailingZeros()
                .toString())
            .add(request
                .getCommissionAmount()
                .stripTrailingZeros()
                .toString())
            .add(secret)
            .toString();
        return algorithmService.sha256(target);
    }
}
