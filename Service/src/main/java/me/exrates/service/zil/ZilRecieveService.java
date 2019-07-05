package me.exrates.service.zil;

import com.firestack.laksaj.blockchain.BlockList;
import com.firestack.laksaj.blockchain.BlockchainInfo;
import com.firestack.laksaj.blockchain.TxBlock;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class ZilRecieveService {

    private static HttpProvider client;

    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
    }

    public static void main(String[] args) throws IOException, ZilliqaAPIException {
        ZilRecieveService zilRecieveService = new ZilRecieveService();
        zilRecieveService.init();
//        zilRecieveService.checkRefills();


        Rep<TxBlock> txBlock = client.getTxBlock("160590");
//        Rep<TxBlock> txBlock = client.getLatestTxBlock();
        Rep<String> numTxBlocks = client.getNumTxBlocks();
        Rep<Double> txBlockRate = client.getTxBlockRate();
        Rep<BlockList> blockListing = client.getTxBlockListing(5);
        Rep<String> numTransactions = client.getNumTransactions();
        Rep<BlockchainInfo> blockchainInfo = client.getBlockchainInfo();
        Rep<Transaction> transaction = client.getTransaction("655107c300e86ee6e819af1cbfce097db1510e8cd971d99f32ce2772dcad42f2");
//        System.out.println(new Gson().toJson(transaction));
        System.out.println(new Gson().toJson(numTransactions));
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
