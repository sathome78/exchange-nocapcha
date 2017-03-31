package me.exrates.dao;

import me.exrates.model.SessionParams;

/**
 * Created by maks on 31.03.2017.
 */
public interface SessionParamsDao {

    SessionParams getByUserEmail(String userEmail);

    SessionParams save(SessionParams sessionLifeType);
}
