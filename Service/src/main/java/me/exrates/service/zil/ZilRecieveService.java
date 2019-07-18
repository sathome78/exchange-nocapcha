package me.exrates.service.zil;

import com.firestack.laksaj.account.Wallet;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.firestack.laksaj.transaction.TransactionFactory;
import com.firestack.laksaj.utils.Bech32;
import com.google.gson.Gson;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firestack.laksaj.account.Wallet.pack;

@Service
@Conditional(MonolitConditional.class)
public class ZilRecieveService {

    private static HttpProvider client;

    private static final String MERCHANT_NAME = "ZIL";
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";
    private static final int CONFIRMATIONS = 20;


    @Autowired
    private MerchantSpecParamsDao specParamsDao;
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

    //TODO add scheduler
    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
//        currency = currencyService.findByName(MERCHANT_NAME);
//        merchant = merchantService.findByName(MERCHANT_NAME);
    }



    public static void main(String[] args) throws Exception {
        ZilRecieveService zilRecieveService = new ZilRecieveService();
        zilRecieveService.init();
//        zilRecieveService.checkRefills();


//        System.out.println(Bech32.fromBech32Address("zil1rk0jzyetaur8uwhqdgdenrp8h4fakq87tl0f8l"));
//        Rep<List<List<String>>> transactionList = client.getTransactionsForTxBlock("173651");
//        System.out.println(new Gson().toJson(transactionList));
//
        Rep<Transaction> transaction = client.getTransaction("d2869864722119d4078d0628a7a1597d48c63d9319fe09573f3b561ccc6046ea");
        System.out.println(new Gson().toJson(transaction));
    }

    private void checkRefills() throws IOException, ZilliqaAPIException {
        long lastblock = getLastBaseBlock();
        long blockchainHeight = getBlockchainHeigh();
        List<String> listOfAddress = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());

        while (lastblock < blockchainHeight - CONFIRMATIONS){
            List<List<String>> transactions = getTransactionsList(++lastblock);
            if (transactions != null){
                transactions.forEach(list -> {
                    if (list != null){
                        for (String hash: list) {
                            try {
                                Transaction transaction = client.getTransaction(hash).getResult();
                                String address = Bech32.toBech32Address(transaction.getToAddr());

                                if (listOfAddress.contains(address)){
                                    processTransaction(transaction);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            //todo Проверить вынести сохранение в базу номер блока после цикла
//            saveLastBlock(lastblock);
        }
    }

    private long getLastBaseBlock(){
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private int getBlockchainHeigh() throws IOException, ZilliqaAPIException {
        Rep<String> numTxBlocks = client.getNumTxBlocks();
        return Integer.valueOf(numTxBlocks.getResult());
    }

    private List<List<String>> getTransactionsList(long block) throws IOException {
        Rep<List<List<String>>> blockTransactions = client.getTransactionsForTxBlock(String.valueOf(block));
        return blockTransactions.getResult();
    }

    private void processTransaction(Transaction transaction) throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("address", Bech32.toBech32Address(transaction.getToAddr()));
        param.put("hash", transaction.getID());
        param.put("amount", transaction.getAmount());

        zilService.processPayment(param);
    }

    private void saveLastBlock(long lastBlock){
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(lastBlock));
    }
}
