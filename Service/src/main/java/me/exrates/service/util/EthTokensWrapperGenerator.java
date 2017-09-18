package me.exrates.service.util;

import me.exrates.service.ethTokensWrappers.Eos;
import me.exrates.service.ethTokensWrappers.Rep;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.glassfish.hk2.api.messaging.Topic;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Filter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.web3j.abi.datatypes.Type.MAX_BYTE_LENGTH;
import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;
import static org.web3j.utils.Numeric.hexStringToByteArray;
import static org.web3j.utils.Numeric.toBigInt;

/**
 * Created by Maks on 14.09.2017.
 */
public class EthTokensWrapperGenerator {

    private EthTokensWrapperGenerator() {
    }

    public static void main(String[] args) throws Exception {
       /* SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "d:/eth/eos.bin",
                "d:/eth/eos.abi",
                "-o",
                "c:/Users/Maks/IdeaProjects/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethTokensWrappers"});*/
       exeprimental();
    }


    private static void exeprimental() throws Exception, CipherException, ExecutionException, InterruptedException {
        String RepContract = "0xE94327D07Fc17907b4DB788E5aDf2ed424adDff6";
        String url = "http://163.172.77.155:8545/";
        Web3j web3j = Web3j.build(new HttpService(url));
        Credentials credentials = WalletUtils.loadCredentials("sprinter31313",
                "c:/Users/Maks/AppData/Roaming/Ethereum/keystore/UTC--2017-09-14T08-03-01.933401300Z--85c481f3c74cbd72d0bf84ffd68a5cc608c4d700");
        Rep contract = Rep.load("0xE94327D07Fc17907b4DB788E5aDf2ed424adDff6", web3j, credentials, GAS_PRICE, GAS_LIMIT);
        Future<Bool> future = contract.initialized();
        System.out.println("contract initialized " + future.get().getValue());


     /*   Это подписка на транзы токена, где нет хэша транзакций*/
      /*  rx.Observable<Rep.TransferEventResponse> observable = contract.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST);
        observable.subscribe(p->System.out.println(p.from.toString() + " " + p.to.toString() + " " + p.value.getValue() + p.txHash));
     */   /*
         подписка через фильтр, тоже не возвращает хэш
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0");
        web3j.ethLogObservable(filter).subscribe(log -> {
            System.out.println(log.getBlockNumber() + " " + log.getTransactionHash());
            TransactionReceipt transactionReceipt = new TransactionReceipt();
            transactionReceipt.setLogs(Collections.singletonList(log));
            *//*List<Rep.TransferEventResponse> receipt = contract.getTransferEvents(transactionReceipt);
            receipt.forEach(p->{
                System.out.println(p.value.getValue().toString() + " from " + p.from.toString() + " to "+ p.to.toString());
            });*//*
        });*/

   /*     String data= "0xa9059cbb000000000000000000000000967975346803fbd816c41ad23a7edc67c5b547dc000000000000000000000000000000000000000000000018cdae8c9afa0b0c00";


       /*подписка на все транзы, фильтрует входящие транзы токена rep*/
        Observable<Transaction> observable1;
        Subscription subscription;
        observable1 = web3j.transactionObservable();
        System.out.println("listening...............");
        subscription = observable1.subscribe(transaction -> {
            System.out.println(transaction.getHash());
            if (transaction.getTo() != null && transaction.getTo().equalsIgnoreCase(RepContract)) {
                System.out.println("-----------------------------------------------------------");
               /* prettyPrint(transaction);*/
                try {
                    TransactionReceipt transactionReceipt = new TransactionReceipt();
                    transactionReceipt = web3j.ethGetTransactionReceipt(transaction.getHash()).send().getResult();
                    if (transactionReceipt == null) {
                        System.out.println("receipt null");
                    }
                    if(transactionReceipt != null) {
                        Log log = transactionReceipt.getLogs().get(0);
                        Rep.TransferEventResponse response = extractData(log.getTopics(), log.getData());
                        if (response == null) {
                            System.out.println("response null");
                            return;
                        }
                        BigDecimal amount = Convert.fromWei(response.value.getValue().toString(), Convert.Unit.ETHER);
                        System.out.println("amount conv: " + amount.toString());
                        System.out.println("hash: " + transactionReceipt.getTransactionHash());
                        System.out.println("to: " + response.to.toString());
                        System.out.println("-----------------------------------------------------------");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

      /*  String data= "0xa9059cbb000000000000000000000000967975346803fbd816c41ad23a7edc67c5b547dc000000000000000000000000000000000000000000000018cdae8c9afa0b0c00";

        byte[] bytes = hexStringToByteArray(data);
        BytesType type = new BytesType(bytes, "byte");
        System.out.println(encodeBytes(type));*/



    }

    static String encodeBytes(BytesType bytesType) {
        byte[] value = bytesType.getValue();
        int length = value.length;
        int mod = length % MAX_BYTE_LENGTH;

        byte[] dest;
        if (mod != 0) {
            int padding = MAX_BYTE_LENGTH - mod;
            dest = new byte[length + padding];
            System.arraycopy(value, 0, dest, 0, length);
        } else {
            dest = value;
        }
        return Numeric.toHexStringNoPrefix(dest);
    }



    /*public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l/2];
        for (int i = 0; i < l; i += 2) {
            data[i/2] = (byte) ((Character.digit(hex.charAt(i), 32) << 4)
                    + Character.digit(hex.charAt(i+1), 32));
        }
        return data;
    }*/

    private static Rep.TransferEventResponse extractData(List<String> topics, String data) {
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }
        System.out.println("event signature " + encodedEventSignature);
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                data, event.getNonIndexedParameters());
        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        EventValues eventValues = new EventValues(indexedValues, nonIndexedValues);
        Rep.TransferEventResponse typedResponse = new Rep.TransferEventResponse();
        typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
        typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
        typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
        return typedResponse;
    }


    private static void prettyPrint(Transaction transaction) {
        System.out.println(transaction.getHash() + " "
                + transaction.getBlockHash() + " "
                + transaction.getInput() + " "
                +  transaction.getTo() + " "
                + transaction.getFrom());

    }

}
