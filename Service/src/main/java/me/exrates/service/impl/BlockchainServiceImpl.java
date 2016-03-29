package me.exrates.service.impl;

import com.google.gson.Gson;
import info.blockchain.api.APIException;
import info.blockchain.api.receive.Receive;
import info.blockchain.api.receive.ReceiveResponse;
import info.blockchain.api.wallet.PaymentResponse;
import info.blockchain.api.wallet.Wallet;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingCryptoPaymentDao;
import me.exrates.model.BTCTransaction;
import me.exrates.model.BlockchainPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Email;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.BlockchainService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
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
    private @Value("${password}") String password;
    private @Value("${guid}") String guid;
    private @Value("${serviceUrl}") String serviceUrl;
    private @Value("${identifier}") String identifier;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PendingCryptoPaymentDao pendingCryptoPaymentDao;

    @Autowired
    private BTCTransactionDao btcTransactionDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private ApplicationContext applicationContext;

    private static final BigDecimal SATOSHI = BigDecimal.valueOf(100000000L);
    private static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.CEILING);

    private static final Logger logger = LogManager.getLogger("merchant");

    @Override
    @Transactional
    public BlockchainPayment createPaymentInvoice(CreditsOperation creditsOperation) {
        final Transaction request = transactionService.createTransactionRequest(creditsOperation);
        final String callback = UriComponentsBuilder
                .fromUriString(callbackUrl)
                .queryParam("invoice_id", request.getId())
                .queryParam("secret",secret)
                .build()
                .encode()
                .toString();
        logger.debug(callback);
        try {
            final ReceiveResponse response = Receive.receive(xPub, callback, apiCode);
            final BigDecimal amount = request.getAmount().add(request.getCommissionAmount());
            final BlockchainPayment payment = new BlockchainPayment();
            payment.setAddress(response.getReceivingAddress());
            payment.setAmount(amount);
            payment.setInvoiceId(request.getId());
            pendingCryptoPaymentDao.create(payment);
            return payment;
        } catch (APIException | IOException e) {
            logger.error(e);
            throw new RejectedPaymentInvoice();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BlockchainPayment findByInvoiceId(int invoiceId) {
        final BlockchainPayment pendingPayment = pendingCryptoPaymentDao.findByInvoiceId(invoiceId);
        if (pendingPayment==null) {
            throw new MerchantInternalException("Invalid invoice_id "+invoiceId);
        }
        pendingPayment.setSecret(secret);
        return pendingPayment;
    }

    @Override
    public String sendPaymentNotification(final BlockchainPayment payment,
        final String email, final Locale locale) {
            final String sumWithCurrency = payment.getAmount().stripTrailingZeros() + "BTC";
            final String notification = String.format("Please pay %1s on the wallet %1s", sumWithCurrency,payment.getAddress());
        final Email mail = new Email();
        mail.setTo(email);
        mail.setSubject("Exrates BTC Payment Invoice");
        mail.setMessage(sumWithCurrency);
        try {
            sendMailService.sendMail(mail);
            logger.info("Sended email :"+email);
        } catch (MailException e) {
            logger.error(e);
        }
        return notification;
    }

    @Override
    public Optional<String> notCorresponds(final Map<String, String> pretended,
        final BlockchainPayment actual) {
        final String value = pretended.get("value");
        if (Objects.isNull(value)) {
            return Optional.of("Amount is invalid");
        }
        BigDecimal amount =
            new BigDecimal(value).divide(SATOSHI,MATH_CONTEXT);
        if (Objects.isNull(pretended.get("address")) ||
            !pretended.get("address").equals(actual.getAddress())) {
            return Optional.of("Address is not correct");
        }
        if (Objects.isNull(pretended.get("secret")) ||
            !pretended.get("secret").equals(actual.getSecret())) {
            return Optional.of("Secret is invalid");
        }
        if (amount.compareTo(actual.getAmount())!=0) {
            return Optional.of("Amount is invalid");
        }
        if (Objects.isNull(pretended.get("transaction_hash"))) {
            return Optional.of("Transaction hash missing");
        }
        return Optional.empty();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String approveBlockchainTransaction(final BlockchainPayment payment,
        final Map<String,String> params) {
        if (Objects.isNull(params.get("confirmations")) ||
            Integer.valueOf(params.get("confirmations"))<4) {
            return "Waiting for confirmations";
        }
        final Transaction transaction = transactionService
            .findById(payment.getInvoiceId());
        if (transaction.getOperationType()== OperationType.INPUT){
            pendingCryptoPaymentDao.delete(payment.getInvoiceId());
        }
        transactionService.provideTransaction(transaction);
        final BTCTransaction btcTransaction = new BTCTransaction();
        btcTransaction.setAmount(payment.getAmount());
        btcTransaction.setTransactionId(transaction.getId());
        btcTransaction.setHash(params.get("transaction_hash"));
        btcTransactionDao.create(btcTransaction);
        logger.info("BTC transaction provided "+btcTransaction);
        return "*ok*";
    }

    //// TODO: 3/29/16 Must be removed
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
//        persistBlockchainTransaction(null, btcTransaction);
    }

    //// TODO: 3/29/16 Must be removed
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
