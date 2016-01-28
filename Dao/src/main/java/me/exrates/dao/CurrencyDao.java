package me.exrates.dao;

import java.util.List;

import me.exrates.model.Currency;

public interface CurrencyDao {
	List<Currency> getCurrList();
}