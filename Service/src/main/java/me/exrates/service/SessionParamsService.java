package me.exrates.service;

import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;

import java.util.List;
import java.util.Map;

/**
 * Created by maks on 31.03.2017.
 */
public interface SessionParamsService {
    List<SessionLifeTimeType> getAllByActive(boolean active);

    SessionParams getByUserEmail(String userEmail);

    SessionParams getByEmailOrDefault(String email);

    SessionParams saveOrUpdate(SessionParams sessionParams, String userEmail);

    SessionParams determineSessionParams();

    boolean isSessionTimeValid(int sessionTime);

    boolean isSessionLifeTypeIdValid(int typeId);
}
