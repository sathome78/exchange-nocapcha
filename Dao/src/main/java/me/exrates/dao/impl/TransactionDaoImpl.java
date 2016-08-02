package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class TransactionDaoImpl implements TransactionDao {

    private static final Logger LOG = LogManager.getLogger(TransactionDaoImpl.class);

    protected static RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> {

        final OperationType operationType = OperationType.convert(resultSet.getInt("TRANSACTION.operation_type_id"));

        final Currency currency = new Currency();
        currency.setId(resultSet.getInt("CURRENCY.id"));
        currency.setName(resultSet.getString("CURRENCY.name"));
        currency.setDescription(resultSet.getString("CURRENCY.description"));

        final Merchant merchant = new Merchant();
        merchant.setId(resultSet.getInt("MERCHANT.id"));
        merchant.setName(resultSet.getString("MERCHANT.name"));
        merchant.setDescription(resultSet.getString("MERCHANT.description"));

        final ExOrder order = new ExOrder();
        try {
            order.setId(resultSet.getInt("EXORDERS.id"));
            order.setUserId(resultSet.getInt("EXORDERS.user_id"));
            order.setCurrencyPairId(resultSet.getInt("EXORDERS.currency_pair_id"));
            order.setOperationType(resultSet.getInt("EXORDERS.operation_type_id") == 0 ? null : OperationType.convert(resultSet.getInt("EXORDERS.operation_type_id")));
            order.setExRate(resultSet.getBigDecimal("EXORDERS.exrate"));
            order.setAmountBase(resultSet.getBigDecimal("EXORDERS.amount_base"));
            order.setAmountConvert(resultSet.getBigDecimal("EXORDERS.amount_convert"));
            order.setCommissionFixedAmount(resultSet.getBigDecimal("EXORDERS.commission_fixed_amount"));
            order.setDateCreation(resultSet.getTimestamp("EXORDERS.date_creation") == null ? null : resultSet.getTimestamp("EXORDERS.date_creation").toLocalDateTime());
            order.setDateAcception(resultSet.getTimestamp("EXORDERS.date_creation") == null ? null : resultSet.getTimestamp("EXORDERS.date_acception").toLocalDateTime());
        } catch (SQLException e) {}

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
        transaction.setOrder(order);
        transaction.setCurrency(currency);
        transaction.setProvided(resultSet.getBoolean("provided"));
        transaction.setConfirmation(resultSet.getInt("confirmation"));
        return transaction;
    };
    private final String SELECT_COUNT =
            " SELECT COUNT(*)" +
                    " FROM TRANSACTION " +
                    " INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
                    " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
                    " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
                    " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
                    " LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
                    " LEFT JOIN EXORDERS ON TRANSACTION.order_id = EXORDERS.id ";
    private final String SELECT_ALL =
            " SELECT TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime, " +
                    " TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation, TRANSACTION.order_id, " +
                    " WALLET.id,WALLET.active_balance,WALLET.reserved_balance,WALLET.currency_id," +
                    " COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
                    " COMMISSION.id,COMMISSION.date,COMMISSION.value," +
                    " CURRENCY.id,CURRENCY.description,CURRENCY.name," +
                    " MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
                    " EXORDERS.id, EXORDERS.user_id, EXORDERS.currency_pair_id, EXORDERS.operation_type_id, EXORDERS.exrate, " +
                    " EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, EXORDERS.date_creation, " +
                    " EXORDERS.date_acception " +
                    " FROM TRANSACTION " +
                    " INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
                    " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
                    " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
                    " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
                    " LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
                    " LEFT JOIN EXORDERS ON TRANSACTION.order_id = EXORDERS.id ";
    @Autowired
    MessageSource messageSource;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Transaction create(Transaction transaction) {
        final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
                " commission_id, operation_type_id, currency_id, merchant_id, datetime, order_id, confirmation, provided," +
                " active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before, " +
                " source_type, " +
                " source_id)" +
                "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
                "   :merchant, :datetime, :order_id, :confirmation, :provided," +
                "   :active_balance_before, :reserved_balance_before, :company_balance_before, :company_commission_balance_before," +
                "   :source_type, " +
                "   :source_id)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("userWallet", transaction.getUserWallet().getId());
                put("companyWallet", transaction.getCompanyWallet().getId());
                put("amount", transaction.getAmount());
                put("commissionAmount", transaction.getCommissionAmount());
                put("commission", transaction.getCommission().getId());
                put("operationType", transaction.getOperationType().type);
                put("currency", transaction.getCurrency().getId());
                put("merchant", transaction.getMerchant() == null ? null : transaction.getMerchant().getId());
                put("datetime", transaction.getDatetime() == null ? null : Timestamp.valueOf(transaction.getDatetime()));
                put("order_id", transaction.getOrder() == null ? null : transaction.getOrder().getId());
                put("confirmation", transaction.getConfirmation());
                put("provided", transaction.isProvided());
                put("active_balance_before", transaction.getActiveBalanceBefore());
                put("reserved_balance_before", transaction.getReservedBalanceBefore());
                put("company_balance_before", transaction.getCompanyBalanceBefore());
                put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                put("source_type", transaction.getSourceType() == null ? null : transaction.getSourceType().toString());
                put("source_id", transaction.getSourceId());
            }
        };
        if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder) > 0) {
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
                " MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
                " EXORDERS.id, EXORDERS.user_id, EXORDERS.currency_pair_id, EXORDERS.operation_type_id, EXORDERS.exrate, " +
                " EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, EXORDERS.date_creation, " +
                " EXORDERS.date_acception " +
                " FROM TRANSACTION " +
                " INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
                " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
                " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
                " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
                " LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id" +
                " LEFT JOIN EXORDERS ON TRANSACTION.order_id = EXORDERS.id " +
                " WHERE TRANSACTION.id = :id";
        final Map<String, Integer> params = singletonMap("id", id);
        return jdbcTemplate.queryForObject(sql, params, transactionRowMapper);
    }

    @Override
    public List<Transaction> findAllByUserWallets(List<Integer> walletIds) {
        return findAllByUserWallets(walletIds, 0, Integer.MAX_VALUE).getData();
    }

    @Override
    public PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final int offset, final int limit) {
        /*final String whereClause = "WHERE TRANSACTION.user_wallet_id in (:ids)";
        final String selectLimitedAllSql = new StringJoiner(" ")
                .add(SELECT_ALL)
                .add(whereClause)
                .add("ORDER BY TRANSACTION.datetime DESC, EXORDERS.id DESC LIMIT " + limit + " OFFSET " + offset)
                .toString();
        final String selectAllCountSql = new StringJoiner(" ")
                .add(SELECT_COUNT)
                .add(whereClause)
                .toString();
        final Map<String, List<Integer>> params = Collections.singletonMap("ids", walletIds);
        final PagingData<List<Transaction>> result = new PagingData<>();
        final int total = jdbcTemplate.queryForObject(selectAllCountSql, params, Integer.class);
        result.setData(jdbcTemplate.query(selectLimitedAllSql, params, transactionRowMapper));
        result.setFiltered(total);
        result.setTotal(total);
        return result;*/
        return findAllByUserWallets(walletIds, offset, limit, "");
    }

    @Override
    public PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final int offset,
                                                              final int limit, final String searchValue) {
        LOG.debug(searchValue);
        final String whereClause = "WHERE TRANSACTION.user_wallet_id in (:ids)";
        String searchClause = "";
        if (searchValue.length() > 0) {
            searchClause = " AND (TRANSACTION.datetime LIKE :searchValue " +
                    "OR (SELECT name FROM OPERATION_TYPE WHERE id = TRANSACTION.operation_type_id) LIKE :searchValue " +
                    "OR CURRENCY.name LIKE :searchValue " +
                    "OR TRANSACTION.amount LIKE :searchValue " +
                    "OR TRANSACTION.commission_amount LIKE :searchValue " +
                    "OR MERCHANT.name LIKE :searchValue " +
                    "OR EXORDERS.id LIKE :searchValue ) ";
        }

        final String selectLimitedAllSql = new StringJoiner(" ")
                .add(SELECT_ALL)
                .add(whereClause)
                .add(searchClause)
                .add("ORDER BY TRANSACTION.datetime DESC, EXORDERS.id DESC LIMIT " + limit + " OFFSET " + offset)
                .toString();
        LOG.debug(selectLimitedAllSql);
        final String selectAllCountSql = new StringJoiner(" ")
                .add(SELECT_COUNT)
                .add(whereClause)
                .add(searchClause)
                .toString();
        Map<String, Object> params = new HashMap<>();
        params.put("ids", walletIds);
        params.put("searchValue", "%" + searchValue + "%");
        final PagingData<List<Transaction>> result = new PagingData<>();
        final int total = jdbcTemplate.queryForObject(selectAllCountSql, params, Integer.class);
        result.setData(jdbcTemplate.query(selectLimitedAllSql, params, transactionRowMapper));
        result.setFiltered(total);
        result.setTotal(total);
        LOG.debug(result);
        return result;
    }




    @Override
    public boolean provide(int id) {
        final int PROVIDED = 1;
        final String sql = "UPDATE TRANSACTION SET provided = :provided WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("provided", PROVIDED);
                put("id", id);
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
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
    public void updateTransactionAmount(final int transactionId, final BigDecimal amount, final BigDecimal commission) {
        final String sql = "UPDATE TRANSACTION SET amount = :amount, commission_amount = :commission WHERE id = :id";
        final Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("commission", commission);
        params.put("id", transactionId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateTransactionConfirmations(final int transactionId, final int confirmations) {
        final String sql = "UPDATE TRANSACTION SET confirmation = :confirmations WHERE id  = :id";
        final Map<String, Integer> params = new HashMap<>();
        params.put("id", transactionId);
        params.put("confirmations", confirmations);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale) {
        String sql = " SELECT * " +
                "  FROM " +
                "  ( " +
                "    SELECT null AS date_time, null as transaction_id, " +
                "      WALLET.active_balance AS active_balance_before, WALLET.reserved_balance AS reserved_balance_before, " +
                "      CURRENCY.name AS operation_type_id, " +
                "      null AS amount, null AS commission_amount, " +
                "      null AS source_type, null AS source_id, " +
                "      null AS status_id " +
                "    FROM WALLET  " +
                "    JOIN CURRENCY ON CURRENCY.id=WALLET.currency_id  " +
                "    WHERE WALLET.id=:wallet_id " +
                "  UNION ALL " +
                "    (" +
                "    SELECT TRANSACTION.datetime, TRANSACTION.id, " +
                "      TRANSACTION.active_balance_before, TRANSACTION.reserved_balance_before, " +
                "      TRANSACTION.operation_type_id, " +
                "      TRANSACTION.amount, TRANSACTION.commission_amount, " +
                "      TRANSACTION.source_type, TRANSACTION.source_id, " +
                "      TRANSACTION.status_id " +
                "    FROM TRANSACTION " +
                "    WHERE TRANSACTION.provided=1 AND TRANSACTION.user_wallet_id = :wallet_id " +
                "    ORDER BY -TRANSACTION.datetime ASC, -TRANSACTION.id ASC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset)+
                "    )" +
                "  ) T " +
                "  ORDER BY -date_time ASC, -transaction_id ASC";
        final Map<String, Object> params = new HashMap<>();
        params.put("wallet_id", walletId);
        return jdbcTemplate.query(sql, params, new RowMapper<AccountStatementDto>() {
            @Override
            public AccountStatementDto mapRow(ResultSet rs, int i) throws SQLException {
                AccountStatementDto accountStatementDto = new AccountStatementDto();
                accountStatementDto.setDatetime(rs.getTimestamp("date_time") == null ? null : rs.getTimestamp("date_time").toLocalDateTime());
                accountStatementDto.setTransactionId(rs.getInt("transaction_id"));
                accountStatementDto.setActiveBalanceBefore(BigDecimalProcessing.formatLocale(rs.getBigDecimal("active_balance_before"), locale, true));
                accountStatementDto.setReservedBalanceBefore(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance_before"), locale, true));
                accountStatementDto.setOperationType(rs.getObject("date_time") == null ? rs.getString("operation_type_id") : OperationType.convert(rs.getInt("operation_type_id")).toString(messageSource, locale));
                accountStatementDto.setAmount(rs.getTimestamp("date_time") == null ? null : BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, true));
                accountStatementDto.setCommissionAmount(rs.getTimestamp("date_time") == null ? null : BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, true));
                accountStatementDto.setSourceType(rs.getObject("source_type") == null ? "" : TransactionSourceType.convert(rs.getString("source_type")).toString(messageSource, locale));
                accountStatementDto.setSourceTypeId(rs.getString("source_type"));
                accountStatementDto.setSourceId(rs.getInt("source_id"));
                accountStatementDto.setTransactionStatus(rs.getObject("status_id") == null ? null : TransactionStatus.convert(rs.getInt("status_id")));
                /**/
                int otid = rs.getObject("date_time") == null? 0: rs.getInt("operation_type_id");
                if (otid != 0) {
                    OperationType ot = OperationType.convert(otid);
                    switch (ot) {
                        case INPUT: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                            .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
                            break;
                        }
                        case OUTPUT: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                            .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.SUBTRACT)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
                            break;
                        }
                        case WALLET_INNER_TRANSFER: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                            .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                            .doAction(rs.getBigDecimal("reserved_balance_before"), rs.getBigDecimal("amount"), ActionType.SUBTRACT)
                                            , locale, true));
                            break;
                        }
                    }
                }
                /**/
                return accountStatementDto;
            }
        });
    }
    public List<Transaction> getInvoiceOpenTransactions(){
        String sql = SELECT_ALL + " where TRANSACTION.merchant_id = (select MERCHANT.id " +
                "from MERCHANT where MERCHANT.name = 'Invoice' )";
        Map<String, String> namedParameters = new HashMap<String, String>();
        ArrayList<Transaction> result = (ArrayList<Transaction>) jdbcTemplate.query(sql, new HashMap<String, String>(),
                transactionRowMapper);
        return result;
    }

}