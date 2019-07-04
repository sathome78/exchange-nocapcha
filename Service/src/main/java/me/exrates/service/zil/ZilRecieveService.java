package me.exrates.service.zil;

import com.google.gson.Gson;

public class ZilRecieveService {
    public static void main(String[] args) {
        HttpProvider client = new HttpProvider("https://api.zilliqa.com/");
        Rep<DsBlock> dsBlock = client.getLatestDsBlock();
        System.out.println(new Gson().toJson(dsBlock));
    }
}
