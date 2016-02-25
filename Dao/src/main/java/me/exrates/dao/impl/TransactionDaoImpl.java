package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class TransactionDaoImpl implements TransactionDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Transaction create(Transaction transaction) {
        final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
                "commission_id, operation_type_id, currency_id, merchant_id, datetime)" +
                "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
                "   :merchant, :datetime)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final Map<String, Object> params = unmodifiableMap(of(
                new SimpleEntry<>("userWallet", transaction.getUserWallet().getId()),
                new SimpleEntry<>("companyWallet", transaction.getCompanyWallet().getId()),
                new SimpleEntry<>("amount", transaction.getAmount()),
                new SimpleEntry<>("commissionAmount", transaction.getCommissionAmount()),
                new SimpleEntry<>("commission", transaction.getCommission().getId()),
                new SimpleEntry<>("operationType", transaction.getOperationType().type),
                new SimpleEntry<>("currency", transaction.getCurrency().getId()),
                new SimpleEntry<>("merchant", transaction.getMerchant().getId()),
                new SimpleEntry<>("datetime", Timestamp.valueOf(transaction.getDatetime())))
                .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
        if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder)>0) {
            transaction.setId(keyHolder.getKey().intValue());
            return transaction;
        }
        throw new RuntimeException("Transaction creating failed");
    }

    //// TODO: 2/11/16 Handle this
    @Override
    public List<Transaction> findAllByUserId(int id) {
        final String sql = "SELECT TRANSACTION.id,TRANSACTION.wallet_id,TRANSACTION.amount,TRANSACTION.transaction_type,TRANSACTION.date,COMMISSION.value " +
                "FROM TRANSACTION JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id WHERE wallet_id in (SELECT wallet_id FROM WALLET WHERE WALLET.user_id = :userId)";
        final Map<String,Integer> params = new HashMap<>();
        params.put("userId",id);
        return null;
//        return jdbcTemplate.query(sql,params,(resultSet, i) -> {
//            Transaction transaction = new Transaction();
//            transaction.setAmount(resultSet.getDouble("amount"));
//            transaction.setId(resultSet.getInt("id"));
//            transaction.setWalletId(resultSet.getInt("wallet_id"));
//            String operationType = resultSet.getInt("transaction_type") == 0 ? "Вывод через Yandex.Money" : "Ввод через Yandex.Money";
//            transaction.setOperationType(operationType);
//            transaction.setDate(resultSet.getTimestamp("date").toLocalDateTime());
//            transaction.setCommission(resultSet.getDouble("value"));
//            return transaction;
//        });
    }
}