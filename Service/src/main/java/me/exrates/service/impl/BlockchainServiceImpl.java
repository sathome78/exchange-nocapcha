package me.exrates.service.impl;

import com.google.gson.Gson;
import info.blockchain.api.APIException;
import info.blockchain.api.receive.Receive;
import info.blockchain.api.receive.ReceiveResponse;
import info.blockchain.api.wallet.PaymentResponse;
import info.blockchain.api.wallet.Wallet;
import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingBlockchainPaymentDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
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
import java.util.Map;
import java.util.Optional;

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
    private @Value("${password}") String password;
    private @Value("${guid}") String guid;
    private @Value("${serviceUrl}") String serviceUrl;
    private @Value("${identifier}") String identifier;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PendingBlockchainPaymentDao blockchainPaymentDao;

    @Autowired
    private BTCTransactionDao btcTransactionDao;

    private static final BigDecimal SATOSHI = BigDecimal.valueOf(100000000L);

    private static final Logger logger = LogManager.getLogger(BlockchainServiceImpl.class);

    @Override
    @Transactional
    public Optional<BlockchainPayment> createPaymentInvoice(CreditsOperation creditsOperation) {
        final Transaction request = transactionService.createTransactionRequest(creditsOperation);
        final String callback = UriComponentsBuilder
                .fromUriString(callbackUrl)
                .queryParam("invoice_id", request.getId())
                .queryParam("secret",secret)
                .build()
                .encode()
                .toString();
        try {
            final ReceiveResponse response = Receive.receive(xPub, callback, apiCode);
            final BigDecimal amount = request.getAmount().add(request.getCommissionAmount());
            final BlockchainPayment payment = new BlockchainPayment();
            payment.setAddress(response.getReceivingAddress());
            payment.setAmount(amount);
            payment.setInvoiceId(request.getId());
            blockchainPaymentDao.create(payment);
            return Optional.of(payment);
        } catch (APIException | IOException e) {
            System.out.println(e);
            logger.error(e);
            throw new RejectedPaymentInvoice();
        }
    }

    @Override
    public BlockchainPayment findByInvoiceId(int invoiceId) {
        final BlockchainPayment pendingPayment = blockchainPaymentDao.findByInvoiceId(invoiceId);
        pendingPayment.setSecret(secret);
        return pendingPayment;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void persistBlockchainTransaction(BlockchainPayment payment, BTCTransaction btcTransaction) {
        final Transaction transaction = transactionService.findById(payment.getInvoiceId());
        if (transaction.getOperationType()== OperationType.INPUT){
            blockchainPaymentDao.delete(payment.getInvoiceId());
        }
        transactionService.provideTransaction(transaction);
        btcTransactionDao.create(btcTransaction);
    }

    @Override
    @Transactional
    public void provideOutputPayment(Payment payment, CreditsOperation creditsOperation) {
        final BigDecimal amount = creditsOperation.getAmount().add(creditsOperation.getCommissionAmount());
        final Transaction transactionRequest = transactionService.createTransactionRequest(creditsOperation);
        transactionService.provideTransaction(transactionRequest);
        final String txHash = sendBTC(payment.getDestination(), amount)
                .orElseThrow(RuntimeException::new);
        final BTCTransaction btcTransaction = new BTCTransaction();
        btcTransaction.setTransactionId(transactionRequest.getId());
        btcTransaction.setAmount(amount);
        btcTransaction.setHash(txHash);
        persistBlockchainTransaction(null, btcTransaction);
    }

    private Optional<String> sendBTC(String address,BigDecimal amount) {
        final info.blockchain.api.wallet.Wallet wallet = new Wallet(
                serviceUrl,
                apiCode,
                identifier,
                password);
        final long inSatoshi = amount.multiply(SATOSHI).longValue();
        final PaymentResponse send;
        try {
            send = wallet.send(address, inSatoshi, null, null, null);
        } catch (APIException | IOException e) {
            logger.error(e);
            throw new MerchantInternalException("Failed to bitcoin Output");
        }
        final Map json = new Gson().fromJson(send.getMessage(), Map.class);
        return Optional.ofNullable((String)json.get("tx_hash"));
    }
}