package me.exrates.dao;

import me.exrates.model.IEODetails;

import java.util.Collection;

public interface IeoDetailsRepository {

    IEODetails save(IEODetails ieoDetails);

    IEODetails update(IEODetails ieoDetails);

    Collection<IEODetails> findByCurrencyName(String currencyName);

    IEODetails findOpenIeoByCurrencyName(String currencyName);
}
