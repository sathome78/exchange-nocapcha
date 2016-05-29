package me.exrates.dao;

import me.exrates.model.ReferralTransaction;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralTransactionDao {

    List<ReferralTransaction> findAll();

    List<ReferralTransaction> findAll(int offset, int limit);

    void create(ReferralTransaction referralTransaction);

    void delete(ReferralTransaction referralTransaction);
}
