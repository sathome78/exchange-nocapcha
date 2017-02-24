package me.exrates.service;

import me.exrates.model.enums.UserAgent;

/**
 * Created by OLEG on 07.10.2016.
 */
public interface ApiService {
    String retrieveApplicationKey(UserAgent userAgent);

    boolean appKeyCheckEnabled();
}
