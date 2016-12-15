package me.exrates.service;

import javax.annotation.PostConstruct;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExchangeService {
    @PostConstruct
    void retrieveCurrencies();
}
