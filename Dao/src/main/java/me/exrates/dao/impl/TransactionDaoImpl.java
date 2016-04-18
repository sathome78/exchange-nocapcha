package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.Commission;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.Transaction;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class TransactionDaoImpl implements TransactionDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    protected static RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> {

        final OperationType operationType = resultSet.getInt("TRANSACTION.operation_type_id") == 1 ? OperationType.INPUT :
                OperationType.OUTPUT;

        final Currency currency = new Currency();
        currency.setId(resultSet.getInt("CURRENCY.id"));
        currency.setName(resultSet.getString("CURRENCY.name"));
        currency.setDescription(resultSet.getString("CURRENCY.description"));

        final Merchant merchant = new Merchant();
        merchant.setId(resultSet.getInt("MERCHANT.id"));
        merchant.setName(resultSet.getString("MERCHANT.name"));
        merchant.setDescription(resultSet.getString("MERCHANT.description"));

        final Commission commission = new Commission();
        commission.setId(resultSet.getInt("COMMISSION.id"));
        commission.setOperationType(operationType);
        commission.setValue(resultSet.getBigDecimal("COMMISSION.value"));
        commission.setDateOfChange(resultSet.getTimestamp("COMMISSION.date"));

        final CompanyWallet companyWallet = new CompanyWallet();
        companyWallet.setBalance(resultSet.getBigDecimal("COMPANY_WALLET.balance"));
        companyWallet.setCommissionBalance(resultSet.getBigDecimal("COMPANY_WALLET.commission_balance"));
        companyWallet.setCurrency(currency);
        companyWallet.setId(resultSet.getInt("COMPANY_WALLET.id"));

        final Wallet userWallet = new Wallet();
        userWallet.setActiveBalance(resultSet.getBigDecimal("WALLET.active_balance"));
        userWallet.setReservedBalance(resultSet.getBigDecimal("WALLET.reserved_balance"));
        userWallet.setId(resultSet.getInt("WALLET.id"));
        userWallet.setCurrencyId(currency.getId());

        final Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("TRANSACTION.id"));
        transaction.setAmount(resultSet.getBigDecimal("TRANSACTION.amount"));
        transaction.setCommissionAmount(resultSet.getBigDecimal("TRANSACTION.commission_amount"));
        transaction.setDatetime(resultSet.getTimestamp("TRANSACTION.datetime").toLocalDateTime());
        transaction.setCommission(commission);
        transaction.setCompanyWallet(companyWallet);
        transaction.setUserWallet(userWallet);
        transaction.setOperationType(operationType);
        transaction.setMerchant(merchant);
        transaction.setCurrency(currency);
        transaction.setProvided(resultSet.getBoolean("provided"));
        transaction.setConfirmation(resultSet.getInt("confirmation"));
        return transaction;
    };

    @Override
    public Transaction create(Transaction transaction) {
        final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
                "commission_id, operation_type_id, currency_id, merchant_id, datetime, confirmation)" +
                "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
                "   :merchant, :datetime, :confirmation)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final Map<String, Object> params = new HashMap<String,Object>(){
            {
                put("userWallet", transaction.getUserWallet().getId());
                put("companyWallet", transaction.getCompanyWallet().getId());
                put("amount", transaction.getAmount());
                put("commissionAmount", transaction.getCommissionAmount());
                put("commission", transaction.getCommission().getId());
                put("operationType", transaction.getOperationType().type);
                put("currency", transaction.getCurrency().getId());
                put("merchant", transaction.getMerchant().getId());
                put("datetime", Timestamp.valueOf(transaction.getDatetime()));
                put("confirmation", transaction.getConfirmation());
            }
        };
        if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder)>0) {
            transaction.setId(keyHolder.getKey().intValue());
            return transaction;
        }
        throw new RuntimeException("Transaction creating failed");
    }

    @Override
    public Transaction findById(int id) {
        final String sql = "SELECT TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime,TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation," +
                " WALLET.id,WALLET.active_balance,WALLET.reserved_balance,WALLET.currency_id," +
                " COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
                " COMMISSION.id,COMMISSION.date,COMMISSION.value," +
                " CURRENCY.id,CURRENCY.description,CURRENCY.name," +
                " MERCHANT.id,MERCHANT.name,MERCHANT.description " +
                " FROM TRANSACTION INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
                " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
                " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
                " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
                " INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id WHERE TRANSACTION.id = :id";
        final Map<String, Integer> params = singletonMap("id", id);
        return jdbcTemplate.queryForObject(sql, params, transactionRowMapper);
    }

    @Override
    public List<Transaction> findAllByUserWallets(List<Integer> walletIds) {
        final String sql = "SELECT TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime,TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation," +
                " WALLET.id,WALLET.active_balance,WALLET.reserved_balance,WALLET.currency_id," +
                " COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
                " COMMISSION.id,COMMISSION.date,COMMISSION.value," +
                " CURRENCY.id,CURRENCY.description,CURRENCY.name," +
                " MERCHANT.id,MERCHANT.name,MERCHANT.description " +
                " FROM TRANSACTION INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
                " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
                " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
                " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
                " INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id WHERE TRANSACTION.user_wallet_id in (:ids)";
        return jdbcTemplate.query(sql, singletonMap("ids", walletIds),transactionRowMapper);
    }

    @Override
    public boolean provide(int id) {
        final int PROVIDED = 1;
        final String sql = "UPDATE TRANSACTION SET provided = :provided WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("provided",PROVIDED);
                put("id", id);
            }
        };
        return jdbcTemplate.update(sql,params) > 0;
    }



    @Override
    public boolean delete(int id) {
        final String sql = "DELETE FROM TRANSACTION where id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("id", id);
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public void updateTransactionConfirmations(final int transactionId, final int confirmations) {
        final String sql = "UPDATE TRANSACTION SET confirmation = :confirmations WHERE id  = :id";
        final Map<String, Integer> params = new HashMap<>();
        params.put("id", transactionId);
        params.put("confirmations", confirmations);
        jdbcTemplate.update(sql, params);
    }
}
