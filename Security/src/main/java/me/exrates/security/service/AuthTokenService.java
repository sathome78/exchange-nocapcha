package me.exrates.security.service;

import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Created by OLEG on 23.08.2016.
 */
public interface AuthTokenService {

    Optional<AuthTokenDto> retrieveToken(String username, String password);
    UserDetails getUserByToken(String token);
    boolean validate(String token);


}
