package me.exrates.service.impl;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.EDRCService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static java.util.Objects.isNull;
import static me.exrates.service.util.OkHttpUtils.stringifyBody;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/merchants/edrcoin.properties")
public class EDRCServiceImpl implements EDRCService {

    private @Value("${edrcoin.id}") String id;
    private @Value("${edrcoin.account}") String account;
    private @Value("${edrcoin.key}") String key;

    private final OkHttpClient client = new OkHttpClient();

    private static final String REGEX = ".*";
    private static final Logger LOG = LogManager.getLogger("merchant");
    private static final MathContext MATH_CONTEXT = new MathContext(9, RoundingMode.CEILING);

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private PendingPaymentDao pendingPaymentDao;

    @Autowired
    private TransactionService transactionService;

    @Override
    @Transactional
    public PendingPayment createPaymentInvoice(final CreditsOperation creditsOperation) {
        final Transaction transaction = transactionService
            .createTransactionRequest(creditsOperation);
        final String xml = buildEDRCAddressXML(transaction.getId());
        final String response = sendRequest(xml,
            "http://api.blockchain.mn/merchant/coin/get_new_address");
        try {
            final PendingPayment payment = new PendingPayment();
            final Map<String, String> params = new HashMap<>();
            params.put("address", "//address/text()");
            params.put("error", "//error_msg/text()");
            final Map<String, String> result = evaluateXpath(response, params);
            if (!isNull(result.get("error"))) {
                throw new MerchantInternalException("Edr-coin responded with error: " + result.get("error"));
            }
            final String hash = computePaymentHash(transaction.getId(), id);
            payment.setAddress(result.get("address"));
            payment.setInvoiceId(transaction.getId());
            payment.setTransactionHash(hash);
            pendingPaymentDao.create(payment);
            return payment;
        } catch (final Exception e) {
            throw new MerchantInternalException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PendingPayment findByInvoiceId(final int invoiceId) {
        return pendingPaymentDao.findByInvoiceId(invoiceId)
            .orElseThrow(() ->
                new MerchantInternalException("Invalid invoice_id: " + invoiceId));
    }

    @Override
    @Transactional
    public boolean confirmPayment(final String requestXml,
        final String requestSignature)
    {
        final String xml = algorithmService
            .base64Decode(requestXml);
        final String signature = algorithmService
            .base64Decode(requestSignature);
        if (!Objects.equals(signature,sha1Signature(xml))) {
            LOG.info("Signature is incorrect");
            return false;
        }
        final Map<String,String> xpaths = new HashMap<String,String>() {
            {
                put("amount", "//amount/text()");
                put("merchantId", "//merchant_id/text()");
                put("status", "//status/text()");
                put("address","//address/text()");
                put("confirmations","//confirmations/text()");
            }
        };
        final Map<String,String> result;
        try {
            result = evaluateXpath(xml, xpaths);
        } catch (Exception e) {
            LOG.error(e);
            return false;
        }
        if (!Objects.equals(result.get("status"),"1")) {
            return false;
        }
        final String address = result.get("address");
        final Optional<PendingPayment> optional = pendingPaymentDao
            .findByAddress(address);
        if (!optional.isPresent()) {
            return false;
        }
        final PendingPayment pending = optional.get();
        if (!Objects.equals(pending.getTransactionHash(),
            computePaymentHash(pending.getInvoiceId(),
                result.get("merchantId")))) {
            LOG.error("Payment hash do not match");
            return false;
        }
        final Transaction transaction = transactionService
            .findById(pending.getInvoiceId());
        final BigDecimal currentAmount = new BigDecimal(result.get("amount"), MATH_CONTEXT);
        final BigDecimal targetAmount = transaction.getAmount().add(transaction.getCommissionAmount(), MATH_CONTEXT);
        if (currentAmount.compareTo(targetAmount)!=0) {
            transactionService.updateTransactionAmount(transaction, currentAmount);
        }
        pendingPaymentDao.delete(pending.getInvoiceId());
        transactionService.provideTransaction(transaction);
        return true;
    }

    private String buildEDRCAddressXML(final int requestId) {
        try {
            return new Xembler(
                new Directives()
                    .add("request")
                    .add("merchant_id")
                    .set(id)
                    .up()
                    .add("request_id")
                    .set(requestId))
                .xml()
                .replaceFirst(REGEX, "")
                .trim();
        } catch (ImpossibleModificationException ignore) {
            throw new RuntimeException(); // It will never happen
        }
    }

    protected String buildWithdrawEDRCXml(final int requestId,
        final String address, final BigDecimal amount)
    {
        try {
            final int PRECISION = 6;
            return new Xembler(
                new Directives()
                    .add("request")
                    .add("merchant_id")
                    .set(id)
                    .up()
                    .add("request_id")
                    .set(requestId)
                    .up()
                    .add("address")
                    .set(address)
                    .up()
                    .add("amount")
                    .set(amount.setScale(PRECISION,BigDecimal.ROUND_CEILING))
                    .up()
                    .add("description")
                    .set("Exrates EDRC payment"))
                .xml()
                .replaceFirst(REGEX, "")
                .trim();
        } catch (ImpossibleModificationException ignore) {
            throw new RuntimeException(); // It will never happen
        }
    }

    private String sendRequest(final String xml, final String url) {
        final RequestBody body = new FormEncodingBuilder()
            .add("operation_xml", encodeRequest(xml))
            .add("signature", encodeSignature(xml))
            .build();
        final Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
        try {
            return client
                .newCall(request)
                .execute()
                .body()
                .string();
        } catch (IOException e) {
            throw new MerchantInternalException(e);
        }
    }

    private String encodeRequest(final String xml) {
        return algorithmService.base64Encode(xml);
    }

    private String encodeSignature(final String xml) {
        return Base64.getEncoder()
            .encodeToString(sha1Signature(xml).getBytes());
    }

    private String sha1Signature(final String xml) {
        final String sign = key + xml + key;
        return algorithmService.sha1(sign);
    }

    private static Map<String,String> evaluateXpath(final String xml,
                                                    final Map<String, String> xpaths) throws Exception
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory
            .newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder
            .parse(new InputSource(new StringReader(xml)));
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        final Map<String, String> result = new HashMap<>();
        for (AbstractMap.Entry<String,String> e : xpaths.entrySet()) {
            final XPathExpression compile = xpath.compile(e.getValue());
            final String val = compile.evaluate(document);
            result.put(e.getKey(), !val.isEmpty() ? val : null);
        }
        return result;
    }

    private String computePaymentHash(final int invoiceId,
                                      final String merchantId)
    {
        final String target = new StringJoiner(":")
            .add(String.valueOf(invoiceId))
            .add(String.valueOf(merchantId))
            .add(key)
            .toString();
        return algorithmService.sha256(target);
    }
}
