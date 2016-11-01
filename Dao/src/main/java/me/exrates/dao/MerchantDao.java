package me.exrates.dao;

import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantDao {

    Merchant create(Merchant merchant);

    Merchant findById(int id);

    List<Merchant> findAll();

    List<Merchant> findAllByCurrency(int currencyId);

    BigDecimal getMinSum(int merchant, int currency);

    List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId);

    List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId);

    List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale);
}