package me.exrates.dao.impl;

import me.exrates.dao.ReferralTransactionDao;
import me.exrates.model.ReferralTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ReferralTransactionDaoImpl implements ReferralTransactionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_ALL = " SELECT REFERRAL_LEVEL.id, REFERRAL_LEVEL.level, REFERRAL_LEVEL.percent" +
            " TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime, " +
            " TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation, TRANSACTION.order_id, " +
            " WALLET.id,WALLET.active_balance,WALLET.reserved_balance,WALLET.currency_id," +
            " COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
            " COMMISSION.id,COMMISSION.date,COMMISSION.value," +
            " CURRENCY.id,CURRENCY.description,CURRENCY.name," +
            " MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
            " EXORDERS.id, EXORDERS.user_id, EXORDERS.currency_pair_id, EXORDERS.operation_type_id, EXORDERS.exrate, " +
            " EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, EXORDERS.date_creation, " +
            " EXORDERS.date_acception " +
            " FROM REFERRAL_TRANSACTION" +
            " INNER JOIN TRANSACTION ON REFERRAL_TRANSACTION." +
            " INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
            " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
            " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
            " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
            " LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
            " LEFT JOIN EXORDERS ON TRANSACTION.order_id = EXORDERS.id ";;

    @Autowired
    public ReferralTransactionDaoImpl(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReferralTransaction> findAll() {
        return null;
    }

    @Override
    public List<ReferralTransaction> findAll(final int offset, final int limit) {
        final String sql = "SELECT * FROM REFERRAL_TRANSACTION ";
        return null;
    }

    @Override
    public void create(final ReferralTransaction referralTransaction) {

    }

    @Override
    public void delete(final ReferralTransaction referralTransaction) {

    }
}