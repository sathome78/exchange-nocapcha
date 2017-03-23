package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.*;
import me.exrates.model.util.BigDecimalProcessing;
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

  protected static RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> {

    final OperationType operationType = OperationType.convert(resultSet.getInt("TRANSACTION.operation_type_id"));

    Currency currency = null;
    try {
      resultSet.findColumn("CURRENCY.id");
      currency = new Currency();
      currency.setId(resultSet.getInt("CURRENCY.id"));
      currency.setName(resultSet.getString("CURRENCY.name"));
      currency.setDescription(resultSet.getString("CURRENCY.description"));
    } catch (SQLException e){
      //NOP
    }

    Merchant merchant = null;
    try {
      resultSet.findColumn("MERCHANT.id");
      merchant = new Merchant();
      merchant.setId(resultSet.getInt("MERCHANT.id"));
      merchant.setName(resultSet.getString("MERCHANT.name"));
      merchant.setDescription(resultSet.getString("MERCHANT.description"));
    } catch (SQLException e){
      //NOP
    }

    ExOrder order = null;
    try {
      resultSet.findColumn("EXORDERS.id");
      order = new ExOrder();
      order.setId(resultSet.getInt("EXORDERS.id"));
      order.setUserId(resultSet.getInt("EXORDERS.user_id"));
      order.setCurrencyPairId(resultSet.getInt("EXORDERS.currency_pair_id"));
      order.setOperationType(resultSet.getInt("EXORDERS.operation_type_id") == 0 ? null : OperationType.convert(resultSet.getInt("EXORDERS.operation_type_id")));
      order.setExRate(resultSet.getBigDecimal("EXORDERS.exrate"));
      order.setAmountBase(resultSet.getBigDecimal("EXORDERS.amount_base"));
      order.setAmountConvert(resultSet.getBigDecimal("EXORDERS.amount_convert"));
      order.setCommissionFixedAmount(resultSet.getBigDecimal("EXORDERS.commission_fixed_amount"));
      order.setDateCreation(resultSet.getTimestamp("EXORDERS.date_creation") == null ? null : resultSet.getTimestamp("EXORDERS.date_creation").toLocalDateTime());
      order.setDateAcception(resultSet.getTimestamp("EXORDERS.date_acception") == null ? null : resultSet.getTimestamp("EXORDERS.date_acception").toLocalDateTime());
    } catch (SQLException e) {
      //NOP
    }

    Commission commission = null;
    try {
      resultSet.findColumn("COMMISSION.id");
      commission = new Commission();
      commission.setId(resultSet.getInt("COMMISSION.id"));
      commission.setOperationType(operationType);
      commission.setValue(resultSet.getBigDecimal("COMMISSION.value"));
      commission.setDateOfChange(resultSet.getTimestamp("COMMISSION.date"));
    } catch (SQLException e) {
      //NOP
    }

    CompanyWallet companyWallet = null;
    try {
      resultSet.findColumn("COMPANY_WALLET.id");
      companyWallet = new CompanyWallet();
      companyWallet.setBalance(resultSet.getBigDecimal("COMPANY_WALLET.balance"));
      companyWallet.setCommissionBalance(resultSet.getBigDecimal("COMPANY_WALLET.commission_balance"));
      companyWallet.setCurrency(currency);
      companyWallet.setId(resultSet.getInt("COMPANY_WALLET.id"));
    } catch (SQLException e) {
      //NOP
    }

    Wallet userWallet = null;
    try {
      resultSet.findColumn("WALLET.id");
      userWallet = new Wallet();
      userWallet.setActiveBalance(resultSet.getBigDecimal("WALLET.active_balance"));
      userWallet.setReservedBalance(resultSet.getBigDecimal("WALLET.reserved_balance"));
      userWallet.setId(resultSet.getInt("WALLET.id"));
      userWallet.setCurrencyId(currency.getId());
      User user = new User();
      user.setId(resultSet.getInt("user_id"));
      user.setEmail(resultSet.getString("user_email"));
      userWallet.setUser(user);
    } catch (SQLException e) {
      //NOP
    }

    Transaction transaction = new Transaction();
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
    Integer confirmations = (Integer) resultSet.getObject("confirmation");
    transaction.setConfirmation(confirmations);
    TransactionSourceType sourceType = resultSet.getString("source_type") == null ?
        null : TransactionSourceType.convert(resultSet.getString("source_type"));
    transaction.setSourceType(sourceType);
    transaction.setSourceId(resultSet.getInt("source_id"));
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
          " LEFT JOIN EXORDERS ON TRANSACTION.source_id = EXORDERS.id ";
  private final String SELECT_ALL =
      " SELECT TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime, " +
          " TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation, TRANSACTION.order_id, " +
          " TRANSACTION.source_type, TRANSACTION.source_id, " +
          " WALLET.id, WALLET.active_balance, WALLET.reserved_balance, WALLET.currency_id," +
          " USER.id as user_id, USER.email as user_email," +
          " COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
          " COMMISSION.id,COMMISSION.date,COMMISSION.value," +
          " CURRENCY.id,CURRENCY.description,CURRENCY.name," +
          " MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
          " EXORDERS.id, EXORDERS.user_id, EXORDERS.currency_pair_id, EXORDERS.operation_type_id, EXORDERS.exrate, " +
          " EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, EXORDERS.date_creation, " +
          " EXORDERS.date_acception " +
          " FROM TRANSACTION " +
          " INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
          " INNER JOIN USER ON WALLET.user_id = USER.id" +
          " INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
          " INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
          " INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
          " LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
          " LEFT JOIN EXORDERS ON TRANSACTION.source_id = EXORDERS.id";

  private static final Map<String, String> TABLE_TO_DB_COLUMN_MAP = new HashMap<String, String>() {{

    put("datetime", "TRANSACTION.datetime");
    put("operationType", "TRANSACTION.operation_type_id");
    put("amount", "TRANSACTION.amount");
    put("status", "TRANSACTION.provided");
    put("currency", "CURRENCY.name");
    put("merchant.description", "MERCHANT.description");
    put("commissionAmount", "TRANSACTION.commission_amount");
    put("order", "TRANSACTION.source_id");

  }};

  private static final Map<String, String> SEARCH_CRITERIA = new HashMap<String, String>() {{

    put("provided", "TRANSACTION.provided = :provided");
    put("source_types", "TRANSACTION.source_type IN (:source_types)");
    put("operation_types", "TRANSACTION.operation_type_id IN (:operation_types)");
    put("merchantIds", "TRANSACTION.merchant_id IN (:merchantIds)");
    put("date_from", "TRANSACTION.datetime >= STR_TO_DATE(:date_from, '%Y-%m-%d %H:%i:%s')");
    put("date_to", "TRANSACTION.datetime <= STR_TO_DATE(:date_to, '%Y-%m-%d %H:%i:%s')");
    put("fromAmount", "TRANSACTION.amount >= :fromAmount");
    put("toAmount", "TRANSACTION.amount <= :toAmount");
    put("fromCommissionAmount", "TRANSACTION.commission_amount >= :fromCommissionAmount");
    put("toCommissionAmount", "TRANSACTION.commission_amount <= :toCommissionAmount");

  }};

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
        " source_id, description)" +
        "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
        "   :merchant, :datetime, :order_id, :confirmation, :provided," +
        "   :active_balance_before, :reserved_balance_before, :company_balance_before, :company_commission_balance_before," +
        "   :source_type, " +
        "   :source_id, :description)";
    final KeyHolder keyHolder = new GeneratedKeyHolder();
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("userWallet", transaction.getUserWallet().getId());
        put("companyWallet", transaction.getCompanyWallet() == null ? null : transaction.getCompanyWallet().getId());
        put("amount", transaction.getAmount());
        put("commissionAmount", transaction.getCommissionAmount());
        put("commission", transaction.getCommission() == null ? null : transaction.getCommission().getId());
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
        put("description", transaction.getDescription());
      }
    };
    if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder) > 0) {
      transaction.setId(keyHolder.getKey().intValue());
      return transaction;
    }
    throw new RuntimeException("Transaction creating failed");
  }

  @Override
  public boolean updateForProvided(Transaction transaction) {
    final String sql = "UPDATE TRANSACTION " +
        " SET provided = :provided, " +
        "     active_balance_before = :active_balance_before, " +
        "     reserved_balance_before = :reserved_balance_before, " +
        "     company_balance_before = :company_balance_before, " +
        "     company_commission_balance_before = :company_commission_balance_before, " +
        "     source_type = :source_type, " +
        "     source_id = :source_id, " +
        "     provided_modification_date = NOW() " +
        " WHERE id = :id";
    final int PROVIDED = 1;
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("provided", PROVIDED);
        put("id", transaction.getId());
        put("active_balance_before", transaction.getActiveBalanceBefore());
        put("reserved_balance_before", transaction.getReservedBalanceBefore());
        put("company_balance_before", transaction.getCompanyBalanceBefore());
        put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
        put("source_type", transaction.getSourceType().name());
        put("source_id", transaction.getSourceId());
      }
    };
    return jdbcTemplate.update(sql, params) > 0;
  }

  @Override
  public Transaction findById(int id) {
    final String sql = SELECT_ALL + " WHERE TRANSACTION.id = :id";
    final Map<String, Integer> params = singletonMap("id", id);
    return jdbcTemplate.queryForObject(sql, params, transactionRowMapper);
  }

  @Override
  public List<Transaction> findAllByUserWallets(List<Integer> walletIds) {
    return findAllByUserWallets(walletIds, -1, -1).getData();
  }

  @Override
  public PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final int offset, final int limit) {
    return findAllByUserWallets(walletIds, offset, limit, "", "ASC", null);
  }

  @Override
  public PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final int offset,
                                                            final int limit, String sortColumn, String sortDirection, Locale locale) {

    return findAllByUserWallets(walletIds, null, null, null, null, null, null, null, null, null, offset, limit, sortColumn, sortDirection, locale);
  }

  @Override
  public PagingData<List<Transaction>> findAllByUserWallets(final List<Integer> walletIds, final Integer status,
                                                            final List<TransactionType> types, final List<Integer> merchantIds,
                                                            final String dateFrom, final String dateTo,
                                                            final BigDecimal fromAmount, final BigDecimal toAmount,
                                                            final BigDecimal fromCommissionAmount, final BigDecimal toCommissionAmount,
                                                            final int offset, final int limit,
                                                            String sortColumn, String sortDirection, Locale locale) {
    String sortDBColumn = TABLE_TO_DB_COLUMN_MAP.getOrDefault(sortColumn, "TRANSACTION.datetime");
    final String whereClauseBasic = "WHERE TRANSACTION.user_wallet_id in (:ids)";
    Map<String, Object> params = new HashMap<>();
    params.put("provided", status);
    params.putAll(retrieveTransactionTypeParams(types));
    params.put("merchantIds", merchantIds);
    params.put("date_from", dateFrom);
    params.put("date_to", dateTo);
    params.put("fromAmount", fromAmount);
    params.put("toAmount", toAmount);
    params.put("fromCommissionAmount", fromCommissionAmount);
    params.put("toCommissionAmount", toCommissionAmount);
    String criteria = defineFilterClause(params);
    String filterClause = criteria.isEmpty() ? "" : "AND " + criteria;
    params.put("ids", walletIds);

    StringJoiner sqlJoiner = new StringJoiner(" ")
        .add(SELECT_ALL)
        .add(whereClauseBasic)
        .add(filterClause)
        .add("ORDER BY").add(sortDBColumn).add(sortDirection);
    if (limit > 0) {
      sqlJoiner.add("LIMIT").add(String.valueOf(limit));
    }

    if (offset > 0) {
      sqlJoiner.add("OFFSET").add(String.valueOf(offset));
    }

    final String selectLimitedAllSql = sqlJoiner.toString();
    final String selectAllCountSql = new StringJoiner(" ")
        .add(SELECT_COUNT)
        .add(whereClauseBasic)
        .add(filterClause)
        .toString();
    final PagingData<List<Transaction>> result = new PagingData<>();
    final int total = jdbcTemplate.queryForObject(selectAllCountSql, params, Integer.class);
    result.setData(jdbcTemplate.query(selectLimitedAllSql, params, transactionRowMapper));
    result.setFiltered(total);
    result.setTotal(total);
    return result;

  }

  private Map<String, Object> retrieveTransactionTypeParams(final List<TransactionType> types) {
    Map<String, Object> params = new HashMap<>();
    Set<String> sourceTypes = new HashSet<>();
    Set<Integer> operationTypes = new HashSet<>();
    if (types != null) {
      types.forEach(item -> {
        if (item.getOperationType() != null) {
          operationTypes.add(item.getOperationType().getType());
        }
        if (item.getSourceType() != null) {
          sourceTypes.add(item.getSourceType().toString());
        }
      });
    }
    if (sourceTypes.size() > 0) {
      params.put("source_types", sourceTypes);
    }
    if (operationTypes.size() > 0) {
      params.put("operation_types", operationTypes);
    }
    return params;
  }

  private String defineFilterClause(Map<String, Object> namedParameters) {
    String emptyValue = "";
    StringJoiner stringJoiner = new StringJoiner(" AND ");
    stringJoiner.setEmptyValue(emptyValue);

    namedParameters.forEach((name, value) -> {
      if (checkPresent(value)) {
        stringJoiner.add(SEARCH_CRITERIA.get(name));
      }
    });
    return stringJoiner.toString();

  }

  private boolean checkPresent(Object param) {
    return !(param == null || param.toString().isEmpty());
  }


  @Override
  public boolean provide(int id) {
    final int PROVIDED = 1;
    final String sql = "UPDATE TRANSACTION SET provided = :provided, provided_modification_date = NOW() WHERE id = :id";
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
    final String sql = "UPDATE TRANSACTION" +
        " SET confirmation = :confirmations" +
        " WHERE id  = :id " +
        "       AND confirmation < :confirmations ";
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
        "      null AS status_id, null AS merchant_name, null AS user_id" +
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
        "      TRANSACTION.status_id, MERCHANT.name AS merchant_name, WALLET.user_id " +
        "    FROM TRANSACTION " +
        "    JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
        "    LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id" +
        "    WHERE TRANSACTION.provided=1 AND TRANSACTION.user_wallet_id = :wallet_id " +
        "    ORDER BY -TRANSACTION.datetime ASC, -TRANSACTION.id ASC " +
        (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset) +
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
        TransactionSourceType transactionSourceType = rs.getObject("source_type") == null ? null : TransactionSourceType.convert(rs.getString("source_type"));
        accountStatementDto.setSourceType(transactionSourceType == null ? "" : transactionSourceType.toString(messageSource, locale));
        accountStatementDto.setSourceTypeId(rs.getString("source_type"));
        accountStatementDto.setSourceId(rs.getInt("source_id"));
        accountStatementDto.setTransactionStatus(rs.getObject("status_id") == null ? null : TransactionStatus.convert(rs.getInt("status_id")));
                /**/
        int otid = rs.getObject("date_time") == null ? 0 : rs.getInt("operation_type_id");
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
            case MANUAL: {
              accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                  .formatLocale(BigDecimalProcessing
                          .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                      , locale, true));
              accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
              break;
            }
          }
        }
        String merchantName = rs.getString("merchant_name");
        if (transactionSourceType != TransactionSourceType.MERCHANT) {
          merchantName = accountStatementDto.getSourceType();
        }
        accountStatementDto.setMerchantName(merchantName);
        accountStatementDto.setWalletId(walletId);
        accountStatementDto.setUserId(rs.getInt("user_id"));
                /**/
        return accountStatementDto;
      }
    });
  }

  @Override
  public Integer getStatementSize(Integer walletId) {
    String sql = "SELECT COUNT(*) FROM TRANSACTION WHERE TRANSACTION.provided=1 AND TRANSACTION.user_wallet_id = :wallet_id";
    Map<String, Integer> params = Collections.singletonMap("wallet_id", walletId);
    return jdbcTemplate.queryForObject(sql, params, Integer.class);
  }

  @Override
  public List<Transaction> getOpenTransactionsByMerchant(Merchant merchant) {
    String sql = SELECT_ALL + " where TRANSACTION.merchant_id = (select MERCHANT.id " +
        "from MERCHANT where MERCHANT.name = :merchant) AND TRANSACTION.operation_type_id = 1";
    Map<String, String> namedParameters = new HashMap<String, String>();
    namedParameters.put("merchant", merchant.getName());
    ArrayList<Transaction> result = (ArrayList<Transaction>) jdbcTemplate.query(sql, namedParameters,
        transactionRowMapper);
    return result;
  }

  @Override
  public BigDecimal maxAmount() {
    String sql = "SELECT MAX(TRANSACTION.amount)" +
        " FROM TRANSACTION ";
    return jdbcTemplate.queryForObject(sql, Collections.EMPTY_MAP, BigDecimal.class);

  }

  @Override
  public BigDecimal maxCommissionAmount() {
    String sql = "SELECT MAX(TRANSACTION.commission_amount)" +
        " FROM TRANSACTION ";
    return jdbcTemplate.queryForObject(sql, Collections.EMPTY_MAP, BigDecimal.class);
  }

  @Override
  public void setSourceId(Integer trasactionId, Integer sourceId) {
    final String sql = "UPDATE TRANSACTION SET source_id = :source_id WHERE id  = :id";
    final Map<String, Integer> params = new HashMap<>();
    params.put("id", trasactionId);
    params.put("source_id", sourceId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<TransactionFlatForReportDto> findAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(
      String startDate,
      String endDate,
      Integer operationType,
      List<Integer> roleIdList,
      List<Integer> currencyList,
      List<String> sourceTypeList) {
    String sql = "SELECT  " +
        "         USER.email AS user_email, " +
        "         TX.id AS transaction_id, TX.amount, TX.commission_amount, TX.datetime, " +
        "         TX.operation_type_id, TX.provided, TX.confirmation, TX.operation_type_id, " +
        "         TX.source_type, " +
        "         TX.provided_modification_date, " +
        "         MERCHANT.name AS merchant_name, " +
        "         CURRENCY.name AS currency_name" +
        " FROM TRANSACTION TX  " +
        " JOIN CURRENCY ON CURRENCY.id = TX.currency_id " +
        " JOIN WALLET ON WALLET.id = TX.user_wallet_id " +
        " JOIN USER AS USER ON USER.id = WALLET.user_id " +
        " JOIN MERCHANT ON MERCHANT.id = TX.merchant_id " +
        " WHERE " +
        "    TX.operation_type_id = :operation_type_id " +
        "    AND TX.source_type IN (:source_type_list) AND (TX.currency_id IN (:currency_list)) " +
        "    AND TX.datetime BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s') " +
        (roleIdList.isEmpty() ? "" :
            " AND USER.roleid IN (:role_id_list)");
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("start_date", startDate);
      put("end_date", endDate);
      if (!roleIdList.isEmpty()) {
        put("role_id_list", roleIdList);
      }
      put("currency_list", currencyList);
      put("source_type_list", sourceTypeList);
      put("operation_type_id", operationType);
    }};
    return jdbcTemplate.query(sql, params, new RowMapper<TransactionFlatForReportDto>() {
      @Override
      public TransactionFlatForReportDto mapRow(ResultSet rs, int i) throws SQLException {
        TransactionFlatForReportDto transactionFlatForReportDto = new TransactionFlatForReportDto();
        transactionFlatForReportDto.setTransactionId(rs.getInt("transaction_id"));
        transactionFlatForReportDto.setMerchant(rs.getString("merchant_name"));
        transactionFlatForReportDto.setUserEmail(rs.getString("user_email"));
        transactionFlatForReportDto.setAmount(rs.getBigDecimal("amount"));
        transactionFlatForReportDto.setCommissionAmount(rs.getBigDecimal("commission_amount"));
        transactionFlatForReportDto.setDatetime(rs.getTimestamp("datetime") == null ? null : rs.getTimestamp("datetime").toLocalDateTime());
        transactionFlatForReportDto.setProvidedDate(rs.getTimestamp("provided_modification_date") == null ? null : rs.getTimestamp("provided_modification_date").toLocalDateTime());
        transactionFlatForReportDto.setConfirmation(rs.getInt("confirmation"));
        transactionFlatForReportDto.setProvided(rs.getBoolean("provided"));
        transactionFlatForReportDto.setCurrency(rs.getString("currency_name"));
        transactionFlatForReportDto.setSourceType(TransactionSourceType.valueOf(rs.getString("source_type")));
        transactionFlatForReportDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
        return transactionFlatForReportDto;
      }
    });
  }

  @Override
  public boolean setStatusById(Integer trasactionId, Integer statusId) {
    String sql = "UPDATE TRANSACTION " +
        " SET status_id = :status_id" +
        " WHERE id = :transaction_id ";
    Map<String, Object> params = new HashMap<String, Object>(){{
      put("transaction_id", trasactionId);
      put("status_id", statusId);
    }};
    return jdbcTemplate.update(sql, params) > 0;
  }

}