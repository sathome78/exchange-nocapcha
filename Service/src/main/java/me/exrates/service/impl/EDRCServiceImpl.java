package me.exrates.service.impl;

import com.google.common.io.BaseEncoding;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import me.exrates.dao.PendingCryptoPaymentDao;
import me.exrates.model.BlockchainPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.EDRCService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.MerchantInternalException;
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

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/${spring.profile.active}/merchants/edrcBlockchain.properties")
public class EDRCServiceImpl implements EDRCService {

    private @Value("${id}") String id;
    private @Value("${account}") String account;
    private @Value("${key}") String key;

    private static final String REGEX = ".*";
    private final int PRESISION = 6;
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private PendingCryptoPaymentDao pendingCryptoPaymentDao;

    @Autowired
    private TransactionService transactionService;

    @Override
    @Transactional
    public BlockchainPayment createPaymentInvoice(final CreditsOperation creditsOperation) {
        final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        final String xml = buildEDRCAddressXML(transaction.getId());
        final String request = sendRequest(xml, "http://api.blockchain.mn/merchant/coin/reffil");
        try {
            final BigDecimal amount = transaction.getAmount().add(transaction.getCommissionAmount());
            final BlockchainPayment payment = new BlockchainPayment();
            final String address;
            address = evaluateXpath(request, Collections.singletonMap("address", "//address/text()"))
                .get("address");
            payment.setAddress(address);
            payment.setAmount(amount);
            payment.setInvoiceId(transaction.getId());
            return pendingCryptoPaymentDao.create(payment);
        } catch (final Exception e) {
            throw new MerchantInternalException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BlockchainPayment findByInvoiceId(final int invoiceId) {
        final BlockchainPayment pendingPayment = pendingCryptoPaymentDao.findByInvoiceId(invoiceId);
        pendingPayment.setSecret(key);
        return pendingPayment;
    }

    @Override
    @Transactional
    public void provideOutputPayment(final Payment payment, final CreditsOperation creditsOperation) {
        final BigDecimal amount = creditsOperation.getAmount().add(creditsOperation.getCommissionAmount());
        final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        transactionService.provideTransaction(transaction);
        final String xml = buildWithdrawEDRCXml(transaction.getId(), payment.getDestination(), amount);
        sendEDRC(xml);
    }

    @Transactional(propagation = Propagation.NESTED)
    protected void sendEDRC(final String xml) {
        final String response = sendRequest(xml, "http://api.blockchain.mn/merchant/coin/withdraw");
        System.out.println(response);
        try {
            String status = evaluateXpath(response, Collections.singletonMap("status", "//status/text()"))
                .get("status");
            if  (status.equals("0")) {
                String message = "EDRCService exception: "+evaluateXpath(response, Collections.singletonMap("error", "//error_msg/text()"))
                    .get("error");
                throw new MerchantInternalException(message);
            }
        } catch (Exception e) {
            throw new MerchantInternalException();
        }
    }

    protected String buildEDRCAddressXML(final int requestId) {
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

    protected String buildWithdrawEDRCXml(final int requestId,final String address, final BigDecimal amount) {
        try {
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
                    .set(amount.setScale(PRESISION,BigDecimal.ROUND_CEILING))
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

    protected String sendRequest(final String xml,final String url) {
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

    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
//        final String xml = new Xembler(
//            new Directives()
//                .add("request")
//                .add("merchant_id")
//                .set(14)
//                .up()
//                .add("request_id")
//                .set(12345)
//                .up()
//                .add("address")
//                .set("eYCZkYQxhY4L6VkH4AruVJ9APxteBsETLP")
//                .up()
//                .add("amount")
//                .set("0.5")
//                .up()
//                .add("description")
//                .set("Exrates EDRC payment"))
//            .xml()
//            .replaceFirst(REGEX, "")
//            .trim();
        final String xml = "<request>\n" +
            "<merchant_id>14</merchant_id>\n" +
            "<request_id>740</request_id>\n" +
            "<address>eYCZkYQxhY4L6VkH4AruVJ9APxteBsETLP</address>\n" +
            "<amount>0.555000</amount>\n" +
            "<description>Exrates EDRC payment</description>\n" +
            "</request>";
        final String secret = "cfc6ac5b15d16ad4dce2b00ccf6593db";
        final String sign = secret + xml + secret;
        final String hashCode = sha1(sign);
        final String signature = BaseEncoding.base64().encode(hashCode.getBytes());
        final String encoded = BaseEncoding.base64().encode(xml.getBytes());
        System.out.println(signature);
        System.out.println(encoded);
        final RequestBody body = new FormEncodingBuilder()
            .add("operation_xml", encoded)
            .add("signature", signature)
            .build();
        final Request request = new Request.Builder()
            .url("http://api.blockchain.mn/merchant/coin/withdraw")
            .post(body)
            .build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        System.out.println(sign);
        System.out.println(signature);
        String r = response.body().string();
        System.out.println(r);


    }

    protected String encodeRequest(final String xml) {
        return algorithmService.base64Encode(xml);
    }

    protected String encodeSignature(final String xml) {
        final String sign = key + xml + key;
        System.out.println("XML");
        System.out.println(xml);
        System.out.println("SIGN");
        System.out.println(sign);
        final String sha1 = algorithmService.sha1(sign);
        return Base64.getEncoder().encodeToString(sha1.getBytes());
    }

    protected static Map<String,String> evaluateXpath(final String xml, final Map<String,String> xpaths) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.parse(new InputSource(new StringReader(xml)));
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        final Map<String, String> result = new HashMap<>();
        for (AbstractMap.Entry<String,String> e : xpaths.entrySet()) {
            final XPathExpression compile = xpath.compile(e.getValue());
            final String val = compile.evaluate(document);
            result.put(e.getKey(), val);
        }
        return result;
    }
}
