package me.exrates.service.zil;

import com.firestack.laksaj.crypto.KeyTools;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.firestack.laksaj.utils.Bech32;
import com.google.gson.Gson;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZilRecieveService {

    private static final String CURRENCY_NAME = "ZIL";
    private static final int CONFIRMATIONS = 0;
    private static HttpProvider client;

    @Autowired
    private RefillService refillService;
    @Autowired
    private ZilService zilService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(CURRENCY_NAME);
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

        Rep<List<List<String>>> blockTransactions = client.getTransactionsForTxBlock(String.valueOf(172776));
        List<List<String>> transactions = blockTransactions.getResult();
//        zilRecieveService.checkRefills();
        Rep<Transaction> transaction = client.getTransaction("09d6c29d7609b894874d3eae50b7b5b5f5d5005babd5c40bcb4318c2ba638657");
        System.out.println(new Gson().toJson(transaction));
//        System.out.println(Bech32.toBech32Address("796aa72203e1c10d000b8282378d90e0f8afb5f7"));
//        System.out.println(Bech32.toBech32Address("3b4f22cdc93294dff474b37ca2a19e8f02d99aea"));

    }

    private void checkRefills() throws IOException, ZilliqaAPIException {
        int lastblock = 172780;//getLastBaseBlock();
        int blockchainHeight = getBlockchainHeigh();
        List<String> listOfAddress = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());

        while (lastblock < blockchainHeight - CONFIRMATIONS){
            List<List<String>> transactions = getTransactionsList(++lastblock);
            if (transactions != null){
                transactions.forEach(list -> {
                    for (String hash: list) {
                        try {
                            Transaction transaction = client.getTransaction(hash).getResult();
                            String address = Bech32.toBech32Address(transaction.getToAddr());

                            //TODO check method
                            if (listOfAddress.contains(address)){
                                processTransaction(transaction);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            saveLastBlock(lastblock);
        }
    }

    void processTransaction(Transaction transaction) throws RefillRequestAppropriateNotFoundException {
        Map<String, String> param = new HashMap<String, String>();
            param.put("","");

            zilService.processPayment(param);
    }

    private int getLastBaseBlock(){
        return 0;
    }

    private List<List<String>> getTransactionsList(int lastblock) throws IOException {
        Rep<List<List<String>>> blockTransactions = client.getTransactionsForTxBlock(String.valueOf(lastblock));
        List<List<String>> transactions = blockTransactions.getResult();
        return transactions;
    }

    private int getBlockchainHeigh() throws IOException, ZilliqaAPIException {
        Rep<String> numTxBlocks = null;

            numTxBlocks = client.getNumTxBlocks();

        return Integer.valueOf(numTxBlocks.getResult());
    }

    private void saveLastBlock(int lastBlock){

    }
}
