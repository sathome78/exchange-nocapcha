package me.exrates.service.tron;

import org.bitcoinj.core.Base58;
import org.springframework.security.crypto.util.EncodingUtils;

import java.util.Arrays;

public class test {

    public static void main(String[] args) {
        String address = "TKnBaMdvQuT4UvpnrfZbWeqqMkD2ZL92eD";
        System.out.println(Base58.encode(address.getBytes()));
    }
}
