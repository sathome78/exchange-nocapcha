package me.exrates.service.impl;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
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
import java.util.Objects;
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
    private final int PRECISION = 6;
    private final OkHttpClient client = new OkHttpClient();

    private static final int CONFIRMATIONS = 10;
    private static final Logger logger = LogManager.getLogger("merchant");

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
        logger.debug("Builded xml request: "+xml);
        final String response = sendRequest(xml, "http://api.blockchain.mn/merchant/coin/get_new_address");
        try {
            logger.debug("EDR-Coin response: "+response);
            final BigDecimal amount = transaction
                .getAmount().add(transaction.getCommissionAmount());
            final BlockchainPayment payment = new BlockchainPayment();
            final String address = evaluateXpath(response,
                Collections.singletonMap("address", "//address/text()"))
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

    @Override
    @Transactional
    public boolean verifyPayment(final String response) {
        final Map<String,String> xpaths = new HashMap<String,String>() {
            {
                put("merchant_id", "//merchant_id/text()");
                put("address","//address/text()");
                put("amount", "//amount/text()");
                put("status", "//status/text()");
                put("conirmations", "//conirmations/text()");
            }
        };
        final Map<String,String> result;
        try {
            result = evaluateXpath(response, xpaths);
        } catch (Exception e) {
            throw new MerchantInternalException(e);
        }
        final int confirmations = Integer.parseInt(result.get("conirmations")
            .split("/")[0]);
        if (!result.get("merchant_id").equals(id) ||
            !result.get("status").equals("1") ||
            confirmations< CONFIRMATIONS) {
            return false;
        }
        final String address = result.get("address");
        final BlockchainPayment payment = pendingCryptoPaymentDao.findByAddress(address);
        if (Objects.isNull(payment)) {
            return false;
        }
        final BigDecimal thatAmount = new BigDecimal(result.get("amount"));
        if (!payment.getAmount().equals(thatAmount)) {
            return false;
        }
        return true;
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
//        final String xml = "<request>\n" +
//            "        <merchant_id>20</merchant_id>\n" +
//            "        <request_id>111111</request_id>\n" +
//            "    </request>";
//        final String secret = "9b82b1719ecdc08fd341842878e91570";
//        final String sign = secret + xml + secret;
//        final String hashCode = sha1(sign);
//        final String signature = BaseEncoding.base64().encode(hashCode.getBytes());
//        final String encoded = BaseEncoding.base64().encode(xml.getBytes());
//        System.out.println(signature);
//        System.out.println(encoded);
//        final RequestBody body = new FormEncodingBuilder()
//            .add("operation_xml", encoded)
//            .add("signature", signature)
//            .build();
//        final Request request = new Request.Builder()
//            .url("http://api.blockchain.mn/merchant/coin/get_new_address")
//            .post(body)
//            .build();
//        OkHttpClient client = new OkHttpClient();
//        Response response = client.newCall(request).execute();
//        System.out.println(sign);
//        System.out.println(signature);
//        String r = response.body().string();
//        System.out.println(r);
        EDRCServiceImpl edrcService = new EDRCServiceImpl();
        final String xml = "<request>\n" +
            "                <merchant_id>14</merchant_id>\n" +
            "                <address>xxxxxjkhjkkj</address>\n" +
            "                <amount>0.555</amount>\n" +
            "                <createtime>111111</createtime>\n" +
            "                <status>1</status>\n" +
            "               <conirmations>2/10</conirmations>  \n" +
            "                        </request>";

        edrcService.verifyPayment(xml);
    }

    protected String encodeRequest(final String xml) {
        return algorithmService.base64Encode(xml);
    }

    protected String encodeSignature(final String xml) {
        final String sign = key + xml + key;
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
