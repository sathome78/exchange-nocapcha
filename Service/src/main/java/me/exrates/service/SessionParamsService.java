package me.exrates.service;

import me.exrates.model.SessionParams;

/**
 * Created by maks on 31.03.2017.
 */
public interface SessionParamsService {
    SessionParams getByUserEmail(String userEmail);

    SessionParams saveOrUpdate(SessionParams sessionParams, String userEmail);
}
