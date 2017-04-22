package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.MerchantDao;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.MerchantImage;
import me.exrates.model.dto.MerchantCurrencyAutoParamDto;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.MerchantImageShortenedDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j
@Repository
public class MerchantDaoImpl implements MerchantDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  MessageSource messageSource;

  @Override
  public Merchant create(Merchant merchant) {
    final String sql = "INSERT INTO MERCHANT (description, name) VALUES (:description,:name)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("description", merchant.getDescription())
        .addValue("name", merchant.getName());
    if (namedParameterJdbcTemplate.update(sql, params, keyHolder) > 0) {
      merchant.setId(keyHolder.getKey().intValue());
      return merchant;
    }
    return null;
  }

  @Override
  public Merchant findById(int id) {
    final String sql = "SELECT * FROM MERCHANT WHERE id = :id";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("id", id);
      }
    };
    return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Merchant.class));
  }

  @Override
  public Merchant findByName(String name) {
    final String sql = "SELECT * FROM MERCHANT WHERE name = :name";
    final Map<String, String> params = Collections.singletonMap("name", name);
    return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Merchant.class));
  }

  @Override
  public List<Merchant> findAll() {
    final String sql = "SELECT * FROM MERCHANT";
    return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Merchant.class));
  }


  @Override
  public List<Merchant> findAllByCurrency(int currencyId) {
    final String sql = "SELECT * FROM MERCHANT WHERE id in (SELECT merchant_id FROM MERCHANT_CURRENCY WHERE currency_id = :currencyId)";
    Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("currencyId", currencyId);
      }
    };
    try {
      return namedParameterJdbcTemplate.query(sql, params, (resultSet, i) -> {
        Merchant merchant = new Merchant();
        merchant.setDescription(resultSet.getString("description"));
        merchant.setId(resultSet.getInt("id"));
        merchant.setName(resultSet.getString("name"));
        return merchant;
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public BigDecimal getMinSum(int merchant, int currency) {
    final String sql = "SELECT min_sum FROM MERCHANT_CURRENCY WHERE merchant_id = :merchant AND currency_id = :currency";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("merchant", merchant);
        put("currency", currency);
      }
    };
    return namedParameterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
  }

  @Override
  public List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType) {
    String blockClause = "";
    if (operationType == OperationType.INPUT) {
      blockClause = " AND MERCHANT_CURRENCY.refill_block = 0";
    } else if (operationType == OperationType.OUTPUT) {
      blockClause = " AND MERCHANT_CURRENCY.withdraw_block = 0";
    }
    final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description, MERCHANT.simple_invoice, " +
        " MERCHANT_CURRENCY.min_sum, " +
        " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, " +
        " MERCHANT_CURRENCY.merchant_fixed_commission " +
        " FROM MERCHANT JOIN MERCHANT_CURRENCY " +
        " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id WHERE MERCHANT_CURRENCY.currency_id in (:currenciesId)" +
        blockClause +
        " order by MERCHANT.merchant_order";

    try {
      return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("currenciesId", currenciesId), (resultSet, i) -> {
        MerchantCurrency merchantCurrency = new MerchantCurrency();
        merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
        merchantCurrency.setName(resultSet.getString("name"));
        merchantCurrency.setDescription(resultSet.getString("description"));
        merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
        merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
        merchantCurrency.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
        merchantCurrency.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
        merchantCurrency.setFixedMinCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
        merchantCurrency.setSimpleInvoice(resultSet.getBoolean("simple_invoice"));
        final String sqlInner = "SELECT * FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
            " AND currency_id = :currency_id;";
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("merchant_id", resultSet.getInt("merchant_id"));
        params.put("currency_id", resultSet.getInt("currency_id"));
        merchantCurrency.setListMerchantImage(namedParameterJdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImage.class)));
        return merchantCurrency;
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole) {
    String whereClause = currencyId == null ? "" : " WHERE MERCHANT_CURRENCY.currency_id = :currency_id";

    final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name, " +
        "                 MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, " +
        "                 MERCHANT_CURRENCY.withdraw_block, MERCHANT_CURRENCY.refill_block, LIMIT_WITHDRAW.min_sum AS min_withdraw_sum, " +
        "                 LIMIT_REFILL.min_sum AS min_refill_sum, MERCHANT_CURRENCY.merchant_fixed_commission " +
        "                FROM MERCHANT " +
        "                JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
        "                JOIN CURRENCY_LIMIT AS LIMIT_WITHDRAW ON MERCHANT_CURRENCY.currency_id = LIMIT_WITHDRAW.currency_id " +
        "                                  AND LIMIT_WITHDRAW.operation_type_id = 2 AND LIMIT_WITHDRAW.user_role_id = :user_role_id " +
        "                  JOIN CURRENCY_LIMIT AS LIMIT_REFILL ON MERCHANT_CURRENCY.currency_id = LIMIT_REFILL.currency_id " +
        "                                  AND LIMIT_REFILL.operation_type_id = 1 AND LIMIT_REFILL.user_role_id = :user_role_id " + whereClause;
    Map<String, Integer> paramMap = new HashMap<String, Integer>() {{
      put("currency_id", currencyId);
      put("user_role_id", userRole.getRole());
    }};

    try {
      return namedParameterJdbcTemplate.query(sql, paramMap, (resultSet, i) -> {
        MerchantCurrencyApiDto merchantCurrencyApiDto = new MerchantCurrencyApiDto();
        merchantCurrencyApiDto.setMerchantId(resultSet.getInt("merchant_id"));
        merchantCurrencyApiDto.setCurrencyId(resultSet.getInt("currency_id"));
        merchantCurrencyApiDto.setName(resultSet.getString("name"));
        merchantCurrencyApiDto.setMinInputSum(resultSet.getBigDecimal("min_refill_sum"));
        merchantCurrencyApiDto.setMinOutputSum(resultSet.getBigDecimal("min_withdraw_sum"));
        merchantCurrencyApiDto.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
        merchantCurrencyApiDto.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
        merchantCurrencyApiDto.setIsWithdrawBlocked(resultSet.getBoolean("withdraw_block"));
        merchantCurrencyApiDto.setIsRefillBlocked(resultSet.getBoolean("refill_block"));
        merchantCurrencyApiDto.setMinFixedCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
        final String sqlInner = "SELECT id, image_path FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
            " AND currency_id = :currency_id;";
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("merchant_id", resultSet.getInt("merchant_id"));
        params.put("currency_id", resultSet.getInt("currency_id"));
        merchantCurrencyApiDto.setListMerchantImage(namedParameterJdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImageShortenedDto.class)));
        return merchantCurrencyApiDto;
      });
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
  }

  @Override
  public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions() {
    final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name AS merchant_name, " +
        " CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, MERCHANT_CURRENCY.merchant_input_commission," +
        " MERCHANT_CURRENCY.merchant_output_commission, MERCHANT_CURRENCY.withdraw_block, MERCHANT_CURRENCY.refill_block, " +
        " MERCHANT_CURRENCY.merchant_fixed_commission, " +
        " MERCHANT_CURRENCY.withdraw_auto_enabled, MERCHANT_CURRENCY.withdraw_auto_delay_seconds, MERCHANT_CURRENCY.withdraw_auto_threshold_amount " +
        " FROM MERCHANT " +
        "JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
        "JOIN CURRENCY ON MERCHANT_CURRENCY.currency_id = CURRENCY.id AND CURRENCY.hidden != 1 " +
        "ORDER BY merchant_id, currency_id";
    return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
      MerchantCurrencyOptionsDto dto = new MerchantCurrencyOptionsDto();
      dto.setMerchantId(rs.getInt("merchant_id"));
      dto.setCurrencyId(rs.getInt("currency_id"));
      dto.setMerchantName(rs.getString("merchant_name"));
      dto.setCurrencyName(rs.getString("currency_name"));
      dto.setInputCommission(rs.getBigDecimal("merchant_input_commission"));
      dto.setOutputCommission(rs.getBigDecimal("merchant_output_commission"));
      dto.setIsRefillBlocked(rs.getBoolean("refill_block"));
      dto.setIsWithdrawBlocked(rs.getBoolean("withdraw_block"));
      dto.setMinFixedCommission(rs.getBigDecimal("merchant_fixed_commission"));
      dto.setWithdrawAutoEnabled(rs.getBoolean("withdraw_auto_enabled"));
      dto.setWithdrawAutoDelaySeconds(rs.getInt("withdraw_auto_delay_seconds"));
      dto.setWithdrawAutoThresholdAmount(rs.getBigDecimal("withdraw_auto_threshold_amount"));
      return dto;
    });
  }

  @Override
  public boolean checkInputRequests(int currencyId, String email) {
    String sql = "SELECT (SELECT COUNT(*) FROM birzha.TRANSACTION \n" +
        "join WALLET ON(WALLET.id = TRANSACTION.user_wallet_id)\n" +
        "join USER ON(USER.id = WALLET.user_id)\n" +
        " where \n" +
        " TRANSACTION.source_type = 'MERCHANT' and TRANSACTION.provided = 0 \n" +
        " and USER.email = :email and TRANSACTION.currency_id = :currencyId \n" +
        " and SUBSTRING_INDEX(TRANSACTION.datetime, ' ', 1) = CURDATE()) < " +
            "(SELECT CURRENCY_LIMIT.max_daily_request FROM USER \n" +
            "join CURRENCY_LIMIT ON (CURRENCY_LIMIT.user_role_id = USER.roleid)\n" +
            "where USER.email = :email AND operation_type_id = 1 AND currency_id = :currencyId); ";
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("currencyId", currencyId);
    params.put("email", email);
    if (namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) == 0){
      return false;
    }else {
      return true;
    }
  }

  @Override
  public boolean checkOutputRequests(int currencyId, String email) {
    String sql = "select\n" +
            "(SELECT COUNT(*) FROM WITHDRAW_REQUEST \n" +
            "\n" +
            "            join USER ON(USER.id = WITHDRAW_REQUEST.user_id)\n" +
            "             where \n" +
            "             USER.email = :email and WITHDRAW_REQUEST.currency_id = :currencyId \n" +
            "             and SUBSTRING_INDEX(WITHDRAW_REQUEST.date_creation, ' ', 1) = CURDATE()) < \n" +
            "             \n" +
            "(SELECT CURRENCY_LIMIT.max_daily_request FROM USER \n" +
            "join CURRENCY_LIMIT ON (CURRENCY_LIMIT.user_role_id = USER.roleid)\n" +
            "where USER.email = :email AND operation_type_id = 2 AND currency_id = :currencyId) ";
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("currencyId", currencyId);
    params.put("email", email);
    if (namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) == 0){
      return false;
    }else {
      return true;
    }
  }

  @Override
  public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    String fieldToToggle = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY SET " + fieldToToggle + " = !" + fieldToToggle +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setBlockForAll(OperationType operationType, boolean blockStatus) {
    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY SET " + blockField + " = :block";
    Map<String, Integer> params = Collections.singletonMap("block", blockStatus ? 1 : 0);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {
    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "UPDATE MERCHANT_CURRENCY SET " + blockField + " = :block" +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("block", blockStatus ? 1 : 0);
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    String blockField = resolveBlockFieldByOperationType(operationType);
    String sql = "SELECT " + blockField + " FROM MERCHANT_CURRENCY " +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Integer> params = new HashMap<>();
    params.put("merchant_id", merchantId);
    params.put("currency_id", currencyId);
    return namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
  }

  private String resolveBlockFieldByOperationType(OperationType operationType) {
    String blockField;
    switch (operationType) {
      case INPUT:
        blockField = "refill_block";
        break;
      case OUTPUT:
        blockField = "withdraw_block";
        break;
      default:
        throw new IllegalArgumentException("Incorrect operation type!");
    }
    return blockField;
  }

  @Override
  public void setAutoWithdrawParamsByMerchantAndCurrency(
      Integer merchantId,
      Integer currencyId,
      Boolean withdrawAutoEnabled,
      Integer withdrawAutoDelaySeconds,
      BigDecimal withdrawAutoThresholdAmount
  ) {
    String sql = "UPDATE MERCHANT_CURRENCY SET " +
        " withdraw_auto_enabled = :withdraw_auto_enabled, " +
        " withdraw_auto_delay_seconds = :withdraw_auto_delay_seconds, " +
        " withdraw_auto_threshold_amount = :withdraw_auto_threshold_amount " +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("withdraw_auto_enabled", withdrawAutoEnabled);
      put("withdraw_auto_delay_seconds", withdrawAutoDelaySeconds);
      put("withdraw_auto_threshold_amount", withdrawAutoThresholdAmount);
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
    }};
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public MerchantCurrencyAutoParamDto findAutoWithdrawParamsByMerchantAndCurrency(
      Integer merchantId,
      Integer currencyId
  ) {
    String sql = "SELECT withdraw_auto_enabled, withdraw_auto_threshold_amount, withdraw_auto_delay_seconds " +
        " FROM MERCHANT_CURRENCY " +
        " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
    }};
    return namedParameterJdbcTemplate.queryForObject(sql, params, (resultSet, i) -> {
      MerchantCurrencyAutoParamDto dto = new MerchantCurrencyAutoParamDto();
      dto.setWithdrawAutoEnabled(resultSet.getBoolean("withdraw_auto_enabled"));
      dto.setWithdrawAutoThresholdAmount(resultSet.getBigDecimal("withdraw_auto_threshold_amount"));
      dto.setWithdrawAutoDelaySeconds(resultSet.getInt("withdraw_auto_delay_seconds"));
      return dto;
    });
  }

  @Override
  public List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime() {
    String sql = "SELECT currency_id, merchant_id, refill_lifetime_hours " +
        " FROM MERCHANT_CURRENCY " +
        " WHERE refill_lifetime_hours IS NOT NULL ";
    return jdbcTemplate.query(sql, (rs, i) -> {
      MerchantCurrencyLifetimeDto result =  new MerchantCurrencyLifetimeDto();
      result.setCurrencyId(rs.getInt("currency_id"));
      result.setMerchantId(rs.getInt("merchant_id"));
      result.setRefillLifetimeHours(rs.getInt("refill_lifetime_hours"));
      return result;
    });
  }

  @Override
  public MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
    String sql = "SELECT currency_id, merchant_id, refill_lifetime_hours " +
        " FROM MERCHANT_CURRENCY " +
        " WHERE " +
        "   merchant_id = :merchant_id " +
        "   AND currency_id = :currency_id";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
    }};
    return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
      MerchantCurrencyLifetimeDto result =  new MerchantCurrencyLifetimeDto();
      result.setCurrencyId(rs.getInt("currency_id"));
      result.setMerchantId(rs.getInt("merchant_id"));
      result.setRefillLifetimeHours(rs.getInt("refill_lifetime_hours"));
      return result;
    });
  }
}