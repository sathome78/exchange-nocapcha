package me.exrates.service.impl;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.service.AdvcashService;
import me.exrates.service.AlgorithmService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestNotFountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/advcashmoney.properties")
@Log4j2
public class AdvcashServiceImpl implements AdvcashService{

    private @Value("${advcash.accountId}") String accountId;
    private @Value("${advcash.accountPass}") String accountPass;
    private @Value("${advcash.payeeName}") String payeeName;
    private @Value("${advcash.paymentSuccess}") String paymentSuccess;
    private @Value("${advcash.paymentFailure}") String paymentFailure;
    private @Value("${advcash.paymentStatus}") String paymentStatus;
    private @Value("${advcash.USDAccount}") String usdCompanyAccount;
    private @Value("${advcash.EURAccount}") String eurCompanyAccount;
    private @Value("${advcash.payeePassword}") String payeePassword;


    private static final Logger logger = LogManager.getLogger(AdvcashServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PendingPaymentDao pendingPaymentDao;

    @Autowired
    private AlgorithmService algorithmService;

    @Override
    public Map<String, String> getAdvcashParams(Transaction transaction) {
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final String companyAccount;
        final Number amountToPay;
        switch (currency) {
            case "GOLD":
                amountToPay = sum.toBigInteger();
                break;
            default:
                amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return new HashMap<String,String>(){
            {
                put("PAYEE_ACCOUNT", currency.equals("USD") ? usdCompanyAccount : eurCompanyAccount);
                put("PAYEE_NAME",payeeName);
                put("PAYMENT_AMOUNT", String.valueOf(amountToPay));
                put("PAYMENT_UNITS",currency);
                put("PAYMENT_ID", String.valueOf(transaction.getId()));
                put("PAYMENT_URL",paymentSuccess);
                put("NOPAYMENT_URL",paymentFailure);
                put("STATUS_URL",paymentStatus);
                put("FORCED_PAYMENT_METHOD","account");
            }
        };
    }

    @Override
    @Transactional
    public RedirectView preparePayment(CreditsOperation creditsOperation, String email) {

        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        Map<String, String> params = getAdvcashParams(transaction);
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);

        Properties properties = new Properties();
        String url = "https://wallet.advcash.com/sci/";
        properties.put("ac_account_email", accountId);
        properties.put("ac_sci_name", payeeName);
        properties.put("ac_amount", amountToPay);
        properties.put("ac_currency", creditsOperation.getCurrency().getName());
        properties.put("ac_order_id", transaction.getId());
        String sign = accountId + ":" + payeeName + ":" + amountToPay
                + ":" + creditsOperation.getCurrency().getName() + ":" + payeePassword
                + ":" + transaction.getId();
        String transactionHash = algorithmService.sha256(sign);
        properties.put("ac_sign", transactionHash);
        properties.put("transaction_hash", transactionHash);

        final PendingPayment payment = new PendingPayment();
        payment.setTransactionHash(transactionHash);
        payment.setInvoiceId(transaction.getId());
        payment.setPendingPaymentStatus(InvoiceRequestStatusEnum.CREATED_USER);
        pendingPaymentDao.create(payment);

        properties.put("ac_success_url", paymentSuccess);
        properties.put("ac_success__method", "POST");
        RedirectView redirectView = new RedirectView(url);
        redirectView.setAttributes(properties);


        return redirectView;
    }

    @Override
    @Transactional
    public Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation) {
        return transactionService.createTransactionRequest(creditsOperation);
    }


    @Override
    @Transactional
    public void provideTransaction(Transaction transaction) {
        if (transaction.getOperationType()== OperationType.INPUT){
            pendingPaymentDao.delete(transaction.getId());
        }
        transactionService.provideTransaction(transaction);
    }

    @Override
    @Transactional
    public void invalidateTransaction(Transaction transaction) {
        transactionService.invalidateTransaction(transaction);
    }

    @Override
    public boolean checkHashTransactionByTransactionId(int invoiceId, String inputHash) {
        Optional<PendingPayment> pendingPayment = pendingPaymentDao.findByInvoiceId(invoiceId);

        if (pendingPayment.isPresent()){
            String transactionHash = pendingPayment.get().getTransactionHash();
            return  transactionHash.equals(inputHash);
        }else {
            return false;
        }
    }

    @Override
    public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for "+withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request){
        throw new NotImplimentedMethod("for "+request);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestNotFountException {
        throw new NotImplimentedMethod("for "+params);
    }
}
