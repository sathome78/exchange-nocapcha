package me.exrates.dao;

import me.exrates.model.Merchant;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantDao {

    Merchant create(Merchant merchant);

    Merchant findById(int id);

    List<Merchant> findAllByCurrency(int currencyId);

    BigDecimal getMinSum(int merchant, int currency);
}