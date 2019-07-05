package me.exrates.service.zil;

import com.firestack.laksaj.blockchain.BlockList;
import com.firestack.laksaj.blockchain.BlockchainInfo;
import com.firestack.laksaj.blockchain.TxBlock;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
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
        System.out.println(new Gson().toJson(txBlock));
        System.out.println(new Gson().toJson(blockListing));
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
