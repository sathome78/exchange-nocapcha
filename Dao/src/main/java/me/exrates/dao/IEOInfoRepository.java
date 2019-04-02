package me.exrates.dao;

import me.exrates.model.IEOInfo;

import java.util.Collection;

public interface IEOInfoRepository {

    IEOInfo createInfo(IEOInfo ieoInfo);

    Collection<IEOInfo> findByCurrencyName(String currencyName);

    IEOInfo findOpenIeoByCurrencyName(String currencyName);
}
