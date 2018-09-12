package me.exrates.dao.impl;

import me.exrates.dao.TemporalTokenDao;
import me.exrates.dao.TemporalTokenService;
import me.exrates.model.TemporalToken;
import org.springframework.beans.factory.annotation.Autowired;

public class TemporalTokenServiceImpl implements TemporalTokenService {

    @Autowired
    TemporalTokenDao temporalTokenDao;
    @Override
    public boolean updateTemporalToken(TemporalToken temporalToken) {
        return false;
    }
}
