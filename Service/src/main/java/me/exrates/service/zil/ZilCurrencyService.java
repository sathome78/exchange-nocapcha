package me.exrates.service.zil;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface ZilCurrencyService {
    String generatePrivateKey();

    String getPublicKeyFromPrivateKey(String privKey);

    String getAddressFromPrivateKey(String privKey);

    String createTransaction(Map<String, String> params) throws Exception;
}
