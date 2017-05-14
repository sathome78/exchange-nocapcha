package me.exrates.dao;

import me.exrates.model.ApiAuthToken;

import java.util.Optional;

/**
 * Created by OLEG on 04.11.2016.
 */
public interface ApiAuthTokenDao {
    long createToken(ApiAuthToken token);

    Optional<ApiAuthToken> retrieveTokenById(Long id);

    boolean prolongToken(Long id);

    boolean deleteExpiredToken(Long id);

    int deleteAllExpired(long tokenDuration);
}
