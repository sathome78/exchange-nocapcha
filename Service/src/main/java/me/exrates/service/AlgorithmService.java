package me.exrates.service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface AlgorithmService {

    String computeMD5Hash(String string);

    String sha1(String string);

    String base64Encode(String string);

    String base64Decode(String string);
}
