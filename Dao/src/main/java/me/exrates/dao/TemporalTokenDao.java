package me.exrates.dao;

import me.exrates.model.TemporalToken;
import org.springframework.stereotype.Repository;


public interface TemporalTokenDao {

    String UPDATE_TEMPORAL_TOKEN = "UPDATE TEMPORAL_TOKEN token SET token.already_used=TRUE WHERE token.value=:value and token.user_id=:userId and token.id=:id";

    boolean updateTemporalToken(TemporalToken token);
}
