package me.exrates.service.zil;

import com.firestack.laksaj.account.Wallet;
import com.firestack.laksaj.crypto.KeyTools;
import com.firestack.laksaj.crypto.Schnorr;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.firestack.laksaj.utils.Bech32;
import com.google.gson.Gson;
import org.web3j.crypto.ECKeyPair;

import javax.annotation.PostConstruct;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

public class ZilRecieveService {

    private static HttpProvider client;

    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
    }

    public static void main(String[] args) throws Exception {
        ZilRecieveService zilRecieveService = new ZilRecieveService();
        zilRecieveService.init();

//        zilRecieveService.checkRefills();
//        generete priv key
        System.out.println(KeyTools.generatePrivateKey());
//        System.out.println(ecKeyPair.getPublicKey());
        System.out.println("....................................");

        Rep<List<List<String>>> transactionList = client.getTransactionsForTxBlock("168406");
        System.out.println(new Gson().toJson(transactionList));
//        Rep<Transaction> transaction = client.getTransaction("9a76aa00a93185ea7d20ae0225ca8ce34bbfcc26ac589303ec6563702cd83c4c");
//        System.out.println(new Gson().toJson(transaction));
//        Rep<TxBlock> txBlock = client.getTxBlock("160590");

//        Rep<Transaction> transaction = client.getTransaction("6b7094293e2991c1d4865e825bfdd59997d5169a6e3e58b7ed88f7d9aa00cc0b");
//        System.out.println(new Gson().toJson(transaction));


        Rep<HttpProvider.BalanceResult> balance = client.getBalance("662c1a7787e37420dc150b60fa529189cf02521f");
        Rep<HttpProvider.BalanceResult> balance2 = client.getBalance32("zil1sgg8m0sgxexza0z2ft4neh26yvldaa9udfgerj");
        System.out.println(balance2 + "   adress!!!");
        System.out.println(Bech32.fromBech32Address("zil1sgg8m0sgxexza0z2ft4neh26yvldaa9udfgerj"));
        System.out.println(Bech32.toBech32Address("662c1a7787e37420dc150b60fa529189cf02521f"));
        System.out.println("..........................................");
        Wallet wallet = new Wallet();
        System.out.println(wallet.createAccount());
        System.out.println(Bech32.toBech32Address(wallet.createAccount()));
        Schnorr schnorr = new Schnorr();
//        schnorr.generateKeyPair();
    }

    static String generateNewAddress(){
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

    void checkRefills(){
        int lastblock = getLastBaseBlock();
        int lastNum = getLastBlockNum();
        System.out.println(lastblock + "    " + lastNum);

    }

    int getLastBaseBlock(){
        return 0;
    }

    void getTxBlock(){

    }

    int getLastBlockNum(){
        return 0;
    }

    void saveLastBlock(){

    }
}
