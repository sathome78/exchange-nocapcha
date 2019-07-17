package me.exrates.service.zil;

import com.firestack.laksaj.crypto.KeyTools;
import com.firestack.laksaj.utils.Bech32;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class ZilCurrencyServiceImpl {

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
}
