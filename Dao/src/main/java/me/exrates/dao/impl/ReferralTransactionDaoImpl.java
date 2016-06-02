package me.exrates.dao.impl;

import me.exrates.dao.ReferralTransactionDao;
import me.exrates.model.ReferralLevel;
import me.exrates.model.ReferralTransaction;
import me.exrates.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.MAX_VALUE;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ReferralTransactionDaoImpl implements ReferralTransactionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_ALL = " SELECT REFERRAL_TRANSACTION.id, USER.email, REFERRAL_TRANSACTION.initiator_id, REFERRAL_TRANSACTION.user_id, REFERRAL_LEVEL.id, REFERRAL_LEVEL.level, REFERRAL_LEVEL.percent," +
            " TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime, " +
            " TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation, TRANSACTION.order_id, " +
            " WALLET.id,WALLET.active_balance,WALLET.reserved_balance,WALLET.currency_id," +
            " COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
            " COMMISSION.id,COMMISSION.date,COMMISSION.value," +
            " CURRENCY.id,CURRENCY.description,CURRENCY.name," +
            " MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
            " EXORDERS.id, EXORDERS.user_id, EXORDERS.currency_pair_id, EXORDERS.operation_type_id, EXORDERS.exrate, " +
            " EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, EXORDERS.date_creation, " +
            " EXORDERS.date_acception" +
            " FROM REFERRAL_TRANSACTION" +
            " INNER JOIN TRANSACTION ON REFERRAL_TRANSACTION.id = TRANSACTION.source_id" +
            " INNER JOIN REFERRAL_LEVEL ON REFERRAL_TRANSACTION.referral_level_id = REFERRAL_LEVEL.id" +
            " INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
            " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
            " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
            " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
            " INNER JOIN USER ON REFERRAL_TRANSACTION.initiator_id = USER.id" +
            " LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
            " LEFT JOIN EXORDERS ON TRANSACTION.order_id = EXORDERS.id ";

    protected static RowMapper<ReferralTransaction> referralTransactionRowMapper = (resultSet, i) -> {
        final ReferralTransaction result = new ReferralTransaction();
        final Transaction transaction = TransactionDaoImpl.transactionRowMapper.mapRow(resultSet, i);
        final ReferralLevel referralLevel = ReferralLevelDaoImpl.referralLevelRowMapper.mapRow(resultSet, i);
        result.setTransaction(transaction);
        result.setId(resultSet.getInt("REFERRAL_TRANSACTION.id"));
        result.setExOrder(transaction.getOrder());
        result.setReferralLevel(referralLevel);
        result.setUserId(resultSet.getInt("REFERRAL_TRANSACTION.user_id"));
        result.setInitiatorId(resultSet.getInt("REFERRAL_TRANSACTION.initiator_id"));
        result.setInitiatorEmail(resultSet.getString("USER.email"));
        return result;
    };

    @Autowired
    public ReferralTransactionDaoImpl(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReferralTransaction> findAll(final int userId) {
        return findAll(userId, 0, MAX_VALUE);
    }

    @Override
    public List<ReferralTransaction> findAll(final int userId, final int offset, final int limit) {
        final String sql = SELECT_ALL + " WHERE REFERRAL_TRANSACTION.user_id = :userId ORDER BY TRANSACTION.datetime DESC  LIMIT :limit OFFSET :offset ";
        final Map<String, Integer> params = new HashMap<>();
        params.put("userId", userId);
        params.put("offset", offset);
        params.put("limit", limit);
        return jdbcTemplate.query(sql, params, referralTransactionRowMapper);
    }

    @Override
    public ReferralTransaction create(final ReferralTransaction referralTransaction) {
        final String sql = "INSERT INTO REFERRAL_TRANSACTION (initiator_id, user_id, order_id, referral_level_id) VALUES (:initiatorId, :userId, :orderId, :refLevelId)";
        final Map<String, Integer> params = new HashMap<>();
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        params.put("initiatorId", referralTransaction.getInitiatorId());
        params.put("userId", referralTransaction.getUserId());
        params.put("orderId", referralTransaction.getExOrder().getId());
        params.put("refLevelId", referralTransaction.getReferralLevel().getId());
        jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        referralTransaction.setId(keyHolder.getKey().intValue());
        return referralTransaction;
    }
}
