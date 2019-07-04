package me.exrates.service.zil;

import com.firestack.laksaj.blockchain.DsBlock;
import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.google.gson.Gson;

import java.io.IOException;

public class ZilRecieveService {
    public static void main(String[] args) throws IOException, ZilliqaAPIException {
        HttpProvider client = new HttpProvider("https://api.zilliqa.com/");
        Rep<DsBlock> dsBlock = client.getLatestDsBlock();
        System.out.println(new Gson().toJson(dsBlock));
    }
}
