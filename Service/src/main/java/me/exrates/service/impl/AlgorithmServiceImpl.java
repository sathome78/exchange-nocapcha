package me.exrates.service.impl;

import me.exrates.service.AlgorithmService;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    private final MessageDigest md5;

    public AlgorithmServiceImpl() {
        try {
            md5  = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new BeanInstantiationException(AlgorithmServiceImpl.class,"Failed to receive MD5 MessageDigest instance");
        }

    }

    @Override
    public String computeMD5Hash(String string) {
        try {
            final byte[] digest = md5.digest(string.getBytes("UTF-8"));
            return byteArrayToHexString(digest);
        } catch (UnsupportedEncodingException ignore) {
            return null;}
    }

    private String byteArrayToHexString(byte[] bytes) {
        final StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}