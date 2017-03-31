package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.SessionParamsDao;
import me.exrates.model.SessionParams;
import me.exrates.service.SessionParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by maks on 31.03.2017.
 */
@Log4j2
@Service
public class SessionParamsServiceImpl implements SessionParamsService {

    @Autowired
    private SessionParamsDao sessionParamsDao;

    @Override
    public SessionParams getByUserEmail(String userEmail) {
        return sessionParamsDao.getByUserEmail(userEmail);
    }

    @Override
    public SessionParams saveOrUpdate(SessionParams sessionParams, String userEmail) {
        return null;
    }

}
