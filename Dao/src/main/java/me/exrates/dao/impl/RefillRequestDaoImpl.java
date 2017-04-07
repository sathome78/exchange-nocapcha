package me.exrates.dao.impl;

import me.exrates.dao.RefillRequestDao;
import me.exrates.model.dto.RefillRequestCreateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


/**
 * created by ValkSam
 */

@Repository
public class RefillRequestDaoImpl implements RefillRequestDao {

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  private MessageSource messageSource;

  @Override
  public int findActiveRequestsByMerchantIdAndUserIdForCurrentDate(Integer merchantId, Integer userId) {
    String sql = "SELECT COUNT(*)" +
        " FROM REFILL_REQUEST RR " +
        " WHERE RR.user_id = :user_id " +
        "       AND RR.merchant_id = :merchant_id " +
        "       AND SUBSTRING_INDEX(RR.date_creation, ' ', 1) = CURDATE() ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("user_id", userId);
      put("merchant_id", merchantId);
    }};
    return jdbcTemplate.queryForObject(sql, params, Integer.class);
  }

  @Override
  public int create(RefillRequestCreateDto request) {
    final String sql = "INSERT INTO REFILL_REQUEST " +
        " (amount, commission, status_id, currency_id, user_id, commission_id, merchant_id, " +
        "  recipient_bank_code, user_full_name, remark, address," +
        "  date_creation, status_modification_date) " +
        " VALUES " +
        " (:amount, :commission, :status_id, :currency_id, :user_id, :commission_id, :merchant_id, " +
        " :recipient_bank_code, :user_full_name, :remark, :address," +
        " NOW(), NOW())";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("amount", request.getAmount())
        .addValue("commission", request.getCommission())
        .addValue("status_id", request.getStatus().getCode())
        .addValue("currency_id", request.getCurrencyId())
        .addValue("user_id", request.getUserId())
        .addValue("commission_id", request.getCommissionId())
        .addValue("merchant_id", request.getMerchantId())
        .addValue("recipient_bank_code", request.getRecipientBankCode())
        .addValue("user_full_name", request.getUserFullName())
        .addValue("remark", request.getRemark())
        .addValue("address", request.getAddress());
    jdbcTemplate.update(sql, params, keyHolder);
    return (int) keyHolder.getKey().longValue();
  }


}

