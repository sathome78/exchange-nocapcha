package me.exrates.dao.impl;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.model.EthereumAccount;
import me.exrates.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EthereumNodeDaoImpl implements EthereumNodeDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final Logger LOG = LogManager.getLogger("node_ethereum");

    public EthereumNodeDaoImpl(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createAddress(EthereumAccount ethereumAccount) {
        final String sql = "INSERT INTO ETHEREUM_TEMP_ACCOUNT (address, user_id, private_key, public_key)" +
                " VALUES (:address, :user_id, :private_key, :public_key)";
        final Map<String, Object> params = new HashMap<>();
        params.put("address", ethereumAccount.getAddress());
        params.put("user_id", ethereumAccount.getUser().getId());
        params.put("private_key", ethereumAccount.getPrivateKey());
        params.put("public_key", ethereumAccount.getPublicKey());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<EthereumAccount> findByAddress(String address){
        final String sql = "select ETHEREUM_TEMP_ACCOUNT.*, USER.id, USER.email from  ETHEREUM_TEMP_ACCOUNT join USER ON(ETHEREUM_TEMP_ACCOUNT.user_id = USER.id) " +
                "WHERE ETHEREUM_TEMP_ACCOUNT.address = :address  limit 1";
        final Map<String, String> params = new HashMap<>();
        params.put("address", address);

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, (resultSet, row) -> {
                EthereumAccount ethereumAccount = new EthereumAccount();
                ethereumAccount.setAddress(resultSet.getString("address"));
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setEmail(resultSet.getString("email"));
                ethereumAccount.setUser(user);
                ethereumAccount.setPrivateKey(new BigInteger(resultSet.getString("private_key")));
                ethereumAccount.setPublicKey(new BigInteger(resultSet.getString("public_key")));
                return ethereumAccount;
            }));

        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<String> findAllAddresses() {
        final String sql = "SELECT address FROM ETHEREUM_TEMP_ACCOUNT";

        return jdbcTemplate.query(sql, (rs, row) -> rs.getString("address"));
    }

    @Override
    public String findUserEmailByAddress(String address){
        final String sql = "select USER.email from  ETHEREUM_TEMP_ACCOUNT join USER ON(ETHEREUM_TEMP_ACCOUNT.user_id = USER.id) " +
                "WHERE ETHEREUM_TEMP_ACCOUNT.address = :address limit 1";
        final Map<String, String> params = new HashMap<>();
        params.put("address", address);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    @Override
    public void createMerchantTransaction(String address, String merchantTransactionId, Integer transactionId) {
        final String sql = "INSERT INTO EDC_MERCHANT_TRANSACTION (merchant_transaction_id, transaction_id, address)" +
                " VALUES (:merchantTransactionId, :transactionId, :address)";
        final Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("merchantTransactionId", merchantTransactionId);
        params.put("transactionId", transactionId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<String> findPendingTransactions() {
        final String sql = "SELECT merchant_transaction_id FROM EDC_MERCHANT_TRANSACTION " +
                "JOIN TRANSACTION ON (TRANSACTION.id = EDC_MERCHANT_TRANSACTION.transaction_id) " +
                "where TRANSACTION.merchant_id = (select id from MERCHANT where MERCHANT.name = 'Ethereum') AND " +
                "TRANSACTION.currency_id = (select id from CURRENCY where CURRENCY.name = 'ETH') AND " +
                "TRANSACTION.provided = 0";

        return jdbcTemplate.query(sql, (rs, row) -> rs.getString("merchant_transaction_id"));
    }

    @Override
    public Integer findTransactionId(String merchantTransactionId){
        final String sql = "select transaction_id from  EDC_MERCHANT_TRANSACTION " +
                "where merchant_transaction_id = :merchant_transaction_id";
        final Map<String, String> params = new HashMap<>();
        params.put("merchant_transaction_id", merchantTransactionId);

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    @Override
    public String findAddressByMerchantTransactionId(String merchantTransactionId){
        final String sql = "select address from  EDC_MERCHANT_TRANSACTION " +
                "where merchant_transaction_id = :merchant_transaction_id";
        final Map<String, String> params = new HashMap<>();
        params.put("merchant_transaction_id", merchantTransactionId);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

}
