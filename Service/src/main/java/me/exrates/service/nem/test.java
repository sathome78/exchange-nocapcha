package me.exrates.service.nem;

import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.NemTransactionException;
import me.exrates.service.exception.NisNotReadyException;
import me.exrates.service.util.RestUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nem.core.crypto.KeyPair;
import org.nem.core.crypto.PrivateKey;
import org.nem.core.crypto.PublicKey;
import org.nem.core.messages.PlainMessage;
import org.nem.core.model.*;
import org.nem.core.model.primitive.Amount;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.time.TimeInstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Maks on 22.07.2017.
 */
public class test {

    static RestTemplate restTemplate = new RestTemplate();
    static String nisServer = "http://127.0.0.1:7890";
    static String address = "TBXU7F3WQJQQRPWJ53UJUYI6IJL2A34YESG7FVA5";
    static Account account =  new Account(new KeyPair(PrivateKey.fromHexString("765b9ef2829ee9c5810b3e59148a15779b059175dd920ab91f859b855afb0eee")));;
    static KeyPair a = new KeyPair(PublicKey.fromHexString("fdb3bbba4d70fb483592c69a9dff6a52bc81499e2a7f6ff094344172a4c818ac"));
    static  Account account2 = new Account(a);
    private final static String pathExtendedInfo = "/node/extended-info";
    private final static String pathPrepareAnounce = "/transaction/prepare-announce";
    private final static String pathGetTransaction = "/transaction/get?hash=";
    private final static String pathGetCurrentBlockHeight = "/chain/last-block";
    private final static String pathGetIncomeTransactions = "/account/transfers/incoming?address=%s";
    private static final int decimals = 6;

    public static void main(String[] args) {
          /*  BigDecimal decimal = new BigDecimal("5554.3423");
            String destinationTag = "234234234234";
            Transaction transaction = prepareTransaction(WithdrawMerchantOperationDto.builder()
                    .accountTo("")
                    .amount(decimal.toPlainString())
                    .destinationTag(destinationTag)
                    .build());

            System.out.println("fee1 " + transaction.getFee().getNumMicroNem());
            TransactionFeeCalculatorAfterFork calculatorAfterFork = new TransactionFeeCalculatorAfterFork();
            System.out.println("fee2 " + countTxFee(decimal, destinationTag));
            Amount afterFee = calculatorAfterFork.calculateMinimumFee(transaction);
            System.out.println("fee after fork " + afterFee.getNumMicroNem() );
            transaction.setFee(afterFee);
            System.out.println("is after fee valid? " + NemGlobals.getTransactionFeeCalculator().isFeeValid(transaction, new BlockHeight(getLastBlockHeight())));
     */
          Address addr = Address.fromEncoded(address);
          PrivateKey privateKey = PrivateKey.fromHexString("765b9ef2829ee9c5810b3e59148a15779b059175dd920ab91f859b855afb0eee");
        System.out.println(privateKey.toString());
        System.out.println(account2.getAddress().getEncoded());
   /*     System.out.println(getIncomeTransactions(address, "9b5a009ee9b45e44ac6bd0fda5ac3188878422148d716d69e57680a5b191b3ae"));
 */   }

    protected static JSONArray getIncomeTransactions(String address, String hash) {
        String url = nisServer.concat(String.format(pathGetIncomeTransactions, address));
        if (!StringUtils.isEmpty(hash)) {
            url = url.concat("&hash=").concat(hash);
        }
        System.out.println(url);
        ResponseEntity<String> response = restTemplate
                .getForEntity(url, String.class);
        if (RestUtil.isError(response.getStatusCode()) || response.getBody().contains("error")) {
            throw new NemTransactionException(response.toString());
        }
        return new JSONObject(response.getBody()).getJSONArray("data");
    }

    private static TransferTransaction prepareTransaction(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        Account reipient = new Account(Address.fromEncoded(withdrawMerchantOperationDto.getAccountTo()));
        TimeInstant currentTimeStamp = getCurrentTimeStamp();
        TransferTransactionAttachment attachment = new TransferTransactionAttachment(new PlainMessage(withdrawMerchantOperationDto.getDestinationTag().getBytes()));
        TransferTransaction transaction = new  TransferTransaction(currentTimeStamp,
                account, reipient, transformToNemAmount(withdrawMerchantOperationDto.getAmount()),  attachment);
        transaction.setDeadline(currentTimeStamp.addHours(2));
        return transaction;
    }

    private static Amount transformToNemAmount(String amount) {
        BigDecimal a = new BigDecimal(amount).setScale(decimals, RoundingMode.HALF_DOWN).multiply(new BigDecimal(1000000));
        return new Amount(a.longValue());
    }

    static BigDecimal countTxFee(BigDecimal amount, String destinationTag) {
        Transaction transaction = prepareTransaction(WithdrawMerchantOperationDto.builder()
                .accountTo("")
                .amount(amount.toPlainString())
                .destinationTag(destinationTag)
                .build());

        Amount feeAmount = NemGlobals.getTransactionFeeCalculator().calculateMinimumFee(transaction);
        System.out.println("is fee valid? " + NemGlobals.getTransactionFeeCalculator().isFeeValid(transaction, new BlockHeight(getLastBlockHeight())));
        return new BigDecimal(transformToString(feeAmount.getNumMicroNem()));
    }

    protected static String transformToString(long nemAmount) {
        BigDecimal a = new BigDecimal(nemAmount).setScale(decimals, RoundingMode.HALF_DOWN).divide(new BigDecimal(1000000));
        return a.toPlainString();
    }

    protected static long getLastBlockHeight() {
        String response = restTemplate.getForObject(nisServer.concat(pathGetCurrentBlockHeight), String.class);
        return new org.json.JSONObject(response).getLong("height");
    }


    protected static TimeInstant getCurrentTimeStamp() {
        try {
            int time = getNodeExtendedInfo().getJSONObject("nisInfo").getInt("currentTime");
            return new TimeInstant(time);
        } catch (Exception e) {
            throw new NisNotReadyException();
        }
    }

    private static JSONObject getNodeExtendedInfo() {
        String response = restTemplate.getForObject(nisServer.concat(pathExtendedInfo), String.class);
        return new org.json.JSONObject(response);
    }
}
