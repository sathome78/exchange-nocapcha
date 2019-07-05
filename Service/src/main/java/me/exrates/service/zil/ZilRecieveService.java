package me.exrates.service.zil;

import com.firestack.laksaj.blockchain.BlockchainInfo;
import com.firestack.laksaj.blockchain.DsBlock;
import com.firestack.laksaj.blockchain.TxBlock;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class ZilRecieveService {

    private static HttpProvider client;

    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
    }

    public static void main(String[] args) throws IOException, ZilliqaAPIException {
        new ZilRecieveService().init();
        Rep<DsBlock> dsBlock = client.getLatestDsBlock();
        Rep<String> numDSBlocks = client.getNumDSBlocks();
        Rep<BlockchainInfo> blockchainInfo = client.getBlockchainInfo();
        Rep<TxBlock> txBlock = client.getLatestTxBlock();
        System.out.println(txBlock);
    }

    int getLastBlock(){
        return 0;
    }

    int getLastBlockNum(){
        return 0;
    }

    void saveLastBlock(){

    }
}
