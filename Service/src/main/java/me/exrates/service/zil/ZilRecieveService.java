package me.exrates.service.zil;

import com.firestack.laksaj.account.Wallet;
import com.firestack.laksaj.blockchain.TxBlock;
import com.firestack.laksaj.crypto.KeyTools;
import com.firestack.laksaj.crypto.Schnorr;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.firestack.laksaj.utils.Bech32;
import com.google.gson.Gson;
import org.web3j.crypto.ECKeyPair;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

public class ZilRecieveService {

    private static HttpProvider client;
    private static final int CONFIRMATIONS = 20;

    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
    }

    public static String generatePrivateKey(){
        String privKey = "";
        try {
            privKey = KeyTools.generatePrivateKey();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return privKey;
    }

    public static String getPublicKeyFromPrivateKey(String privKey){
        return KeyTools.getPublicKeyFromPrivateKey(privKey, true);
    }

    public static String getAddressFromPrivateKey(String privKey){
        String address = KeyTools.getAddressFromPrivateKey(privKey);
        try {
            return Bech32.toBech32Address(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        ZilRecieveService zilRecieveService = new ZilRecieveService();
        zilRecieveService.init();

        zilRecieveService.checkRefills();
        Rep<TxBlock> txBlock = client.getLatestTxBlock();
        System.out.println(new Gson().toJson(txBlock));
        Rep<List<List<String>>> transactionList = client.getTransactionsForTxBlock("172666");
        transactionList.getResult().forEach(list -> {
            System.out.println(list);
                    if (list.contains("05049b0ad4c6411dd5208651c1b191dae8245e9a1d14e640368312375eaa2a5c")) {
                        System.out.println("Contains");
                    }}
            );
//       Rep<List<List<String>>> transactionList = client.getTransactionsForTxBlock("168406");
//        System.out.println(new Gson().toJson(transactionList));
//        Rep<Transaction> transaction = client.getTransaction("9a76aa00a93185ea7d20ae0225ca8ce34bbfcc26ac589303ec6563702cd83c4c");
//        System.out.println(new Gson().toJson(transaction));
//        Rep<TxBlock> txBlock = client.getTxBlock("160590");

//        Rep<Transaction> transaction = client.getTransaction("6b7094293e2991c1d4865e825bfdd59997d5169a6e3e58b7ed88f7d9aa00cc0b");
//        System.out.println(new Gson().toJson(transaction));
    }

    private void checkRefills(){
        int lastblock = 200000;//getLastBaseBlock();
        int blockchainHeight = getBlockchainHeigh();

        while (lastblock < blockchainHeight - CONFIRMATIONS){
            Rep<List<List<String>>> transactionList = null;
            try {
                transactionList = client.getTransactionsForTxBlock(String.valueOf(++lastblock));
            } catch (IOException e) {
                e.printStackTrace();
            }

            saveLastBlock(7);
        }
        System.out.println(lastblock + "    " + blockchainHeight);

    }

    private int getLastBaseBlock(){
        return 0;
    }

    private void getTxBlock(){

    }

    private int getBlockchainHeigh(){
        Rep<String> numTxBlocks = null;
        try {
            numTxBlocks = client.getNumTxBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ZilliqaAPIException e) {
            e.printStackTrace();
        }
        return Integer.valueOf(numTxBlocks.getResult());
    }

    private void saveLastBlock(int lastBlock){

    }
}
