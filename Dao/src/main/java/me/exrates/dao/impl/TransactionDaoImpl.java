package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class TransactionDaoImpl implements TransactionDao {

    @Autowired
    private DataSource dataSource;

    @Override
    public boolean create(Transaction transaction) {
        final String sql = "INSERT INTO TRANSACTION (wallet_id, amount, transaction_type, date, commission_id) " +
                "VALUES (:walletId, :amount, :transactionType, :date, :commission_id)";
        final Map<String,String> params = new HashMap<String,String>(){
            {
                put("walletId",String.valueOf(transaction.getWalletId()));
                put("amount",String.valueOf(transaction.getAmount()));
                put("transactionType",String.valueOf(transaction.getTransactionType().operation));
                put("date",Timestamp.valueOf(transaction.getDate()).toString());
                put("commission_id",String.valueOf(transaction.getCommissionId()));
            }
        };
        return new NamedParameterJdbcTemplate(dataSource).update(sql,params)>0;
    }

    //// TODO: 2/11/16 Handle this
    @Override
    public List<Transaction> findAllByUserId(int id) {
        final String sql = "SELECT TRANSACTION.id,TRANSACTION.wallet_id,TRANSACTION.amount,TRANSACTION.transaction_type,TRANSACTION.date,COMMISSION.value " +
                "FROM TRANSACTION JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id WHERE wallet_id in (SELECT wallet_id FROM WALLET WHERE WALLET.user_id = :userId)";
        final Map<String,Integer> params = new HashMap<>();
        params.put("userId",id);
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate.query(sql,params,(resultSet, i) -> {
            Transaction transaction = new Transaction();
            transaction.setAmount(resultSet.getDouble("amount"));
            transaction.setId(resultSet.getInt("id"));
            transaction.setWalletId(resultSet.getInt("wallet_id"));
            String operationType = resultSet.getInt("transaction_type") == 0 ? "Вывод через Yandex.Money" : "Ввод через Yandex.Money";
            transaction.setOperationType(operationType);
            transaction.setDate(resultSet.getTimestamp("date").toLocalDateTime());
            transaction.setCommission(resultSet.getDouble("value"));
            return transaction;
        });
    }
}