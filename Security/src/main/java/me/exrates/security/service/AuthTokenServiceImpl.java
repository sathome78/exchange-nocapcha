package me.exrates.security.service;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.security.exception.*;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.util.RestPasswordDecodingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by OLEG on 23.08.2016.
 */
@Service
public class AuthTokenServiceImpl implements AuthTokenService {
    private static final Logger logger = LogManager.getLogger("mobileAPI");


    private static final String TOKEN_KEY = "k2j34g5sdfgs8d97"; //currently a random combination of letters and digits
    private static final String CREATE_DATE_KEY = "token_create_date";
    private static final String EXPIRATION_DATE_KEY = "token_expiration_date";
    private static final long TOKEN_DURATION_TIME = 5 * 60L; //SECONDS



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;


    @Override
    public Optional<AuthTokenDto> retrieveToken(String username, String encodedPassword) {
        if (username == null || encodedPassword == null) {
            throw new MissingCredentialException("Credentials missing");
        }
        String password = RestPasswordDecodingUtils.decode(encodedPassword);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            Map<String, Object> tokenData = new HashMap<>();

            tokenData.put("clientType", "user");
            tokenData.put("username", userDetails.getUsername());
            Date creation = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            tokenData.put(CREATE_DATE_KEY, creation.getTime());
            Date expiration = Date.from(LocalDateTime.now().plusSeconds(TOKEN_DURATION_TIME).atZone(ZoneId.systemDefault()).toInstant());
            tokenData.put(EXPIRATION_DATE_KEY, expiration.getTime());
            JwtBuilder jwtBuilder = Jwts.builder();
            jwtBuilder.setExpiration(expiration);
            jwtBuilder.setClaims(tokenData);
            AuthTokenDto authTokenDto = new AuthTokenDto(jwtBuilder.signWith(SignatureAlgorithm.HS512, TOKEN_KEY).compact(), expiration.getTime());
            return Optional.of(authTokenDto);
        } else {
            throw new IncorrectPasswordException("Incorrect password");
        }

    }

    @Override
    public UserDetails getUserByToken(String token) {
        if (token == null) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(TOKEN_KEY).parse(token).getBody();
            claims.forEach((key, value) -> logger.info(key + " :: " + value));
        } catch (Exception ex) {
            throw new TokenException("Token corrupted", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
        if (claims.get("token_expiration_date", Long.class) == null) {
            throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
        Date expiredDate = new Date(claims.get(EXPIRATION_DATE_KEY, Long.class));
        if (expiredDate.after(new Date())) {
            return userDetailsService.loadUserByUsername(claims.get("username", String.class));
        } else {
            throw new TokenException("Token expired", ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
        }


    }

    @Override
    public boolean validate(String token) {
        return false;
    }


}
