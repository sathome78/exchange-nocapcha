package me.exrates.dao;

import me.exrates.model.Merchant;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantDao {

    Merchant create(Merchant merchant);

    List<Merchant> findAllByCurrency(int currencyId);
}