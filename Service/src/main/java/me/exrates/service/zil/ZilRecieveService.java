package me.exrates.service.zil;

import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;

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


//        Rep<TxBlock> txBlock = client.getTxBlock("160590");

        Rep<Transaction> transaction = client.getTransaction("6b7094293e2991c1d4865e825bfdd59997d5169a6e3e58b7ed88f7d9aa00cc0b");
        System.out.println(new Gson().toJson(transaction));

//        Rep<TransactionList> transactionList = client.getRecentTransactions();
//        System.out.println(new Gson().toJson(transactionList));
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
