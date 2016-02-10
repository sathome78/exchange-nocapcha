package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
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
}