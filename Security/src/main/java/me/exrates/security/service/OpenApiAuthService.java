package me.exrates.security.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface OpenApiAuthService {
    UserDetails getUserByPublicKey(String method, String endpoint, Long timestamp, String publicKey, String signatureHex);
}
