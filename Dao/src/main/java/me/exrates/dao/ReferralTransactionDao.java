package me.exrates.dao;

import me.exrates.model.ReferralTransaction;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface ReferralTransactionDao {

    List<ReferralTransaction> findAll(int userId);

    List<ReferralTransaction> findAll(int userId, int offset, int limit);

    ReferralTransaction create(ReferralTransaction referralTransaction);
}
