package me.exrates.service.util;

import java.util.Base64;

/**
 * Created by OLEG on 01.09.2016.
 */
public class RestPasswordDecodingUtils {
    private static final String PASSWORD_ENCODE_KEY = "3255c246-4b9f-43a5-b2dd-63524f959953";


    public static String decode(String password) {
        final byte[] txt = Base64.getDecoder().decode(password);
        final byte[] key = PASSWORD_ENCODE_KEY.getBytes();
        final byte[] result = new byte[txt.length];
        for (int i = 0; i < txt.length; i++)
            result[i] = (byte) (txt[i] ^ key[i % key.length]);
        return new String(result);
    }
}
