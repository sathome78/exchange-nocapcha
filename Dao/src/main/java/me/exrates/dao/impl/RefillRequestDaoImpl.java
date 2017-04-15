package me.exrates.dao.impl;

import me.exrates.dao.RefillRequestDao;
import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.OperationUserDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;


/**
 * created by ValkSam
 */

@Repository
public class RefillRequestDaoImpl implements RefillRequestDao {

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private NamedParameterJdbcTemplate parameterJdbcTemplate;

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
    return parameterJdbcTemplate.queryForObject(sql, params, Integer.class);
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
    parameterJdbcTemplate.update(sql, params, keyHolder);
    return (int) keyHolder.getKey().longValue();
  }

  @Override
  public List<InvoiceBank> findInvoiceBanksByCurrency(Integer currencyId) {
    final String sql = "SELECT id, currency_id, name, account_number, recipient " +
        " FROM INVOICE_BANK " +
        " WHERE currency_id = :currency_id";
    final Map<String, Integer> params = Collections.singletonMap("currency_id", currencyId);
    return parameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
      InvoiceBank bank = new InvoiceBank();
      bank.setId(rs.getInt("id"));
      bank.setName(rs.getString("name"));
      bank.setCurrencyId(rs.getInt("currency_id"));
      bank.setAccountNumber(rs.getString("account_number"));
      bank.setRecipient(rs.getString("recipient"));
      return bank;
    });
  }

  @Override
  public Optional<LocalDateTime> getAndBlockByIntervalAndStatus(
      Integer merchantId,
      Integer currencyId,
      Integer intervalHours,
      List<Integer> statusIdList) {
    LocalDateTime nowDate = jdbcTemplate.queryForObject("SELECT NOW()", LocalDateTime.class);
    String sql =
        " SELECT COUNT(*) " +
            " FROM REFILL_REQUEST " +
            " WHERE " +
            "   merchant_id = :merchant_id " +
            "   AND currency_id = :currency_id" +
            "   AND status_modification_date <= DATE_SUB(:now_date, INTERVAL " + intervalHours + " HOUR ) " +
            "   AND status_id IN (:istatus_id_list)" +
            " FOR UPDATE"; //FOR UPDATE Important!
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
      put("now_date", nowDate);
      put("status_id_list", statusIdList);
    }};
    return Optional.ofNullable(parameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0 ? nowDate : null);
  }

  @Override
  public void setNewStatusByDateIntervalAndStatus(
      Integer merchantId,
      Integer currencyId,
      LocalDateTime nowDate,
      Integer intervalHours,
      Integer newStatusId,
      List<Integer> statusIdList) {
    final String sql =
        " UPDATE REFILL_REQUEST " +
            " SET status_id = :status_id, " +
            "     status_modification_date = :now_date " +
            " WHERE " +
            "   merchant_id = :merchant_id " +
            "   AND currency_id = :currency_id" +
            "   AND status_modification_date <= DATE_SUB(:now_date, INTERVAL " + intervalHours + " HOUR) " +
            "   AND status_id IN (:status_id_list)";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
      put("now_date", nowDate);
      put("status_id", newStatusId);
      put("status_id_list", statusIdList);
    }};
    parameterJdbcTemplate.update(sql, params);
  }

  @Override
  public List<OperationUserDto> findInvoicesListByStatusChangedAtDate(
      Integer merchantId,
      Integer currencyId,
      Integer statusId,
      LocalDateTime dateWhenChanged) {
    String sql =
        " SELECT id, user_id " +
            " FROM REFILL_REQUEST " +
            " WHERE " +
            "   merchant_id = :merchant_id " +
            "   AND currency_id = :currency_id" +
            "   AND status_modification_date = :date " +
            "   AND status_id = :status_id";
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
      put("date", dateWhenChanged);
      put("status_id", statusId);
    }};
    try {
      return parameterJdbcTemplate.query(sql, params, (resultSet, i) -> {
        OperationUserDto operationUserDto = new OperationUserDto();
        operationUserDto.setUserId(resultSet.getInt("user_id"));
        operationUserDto.setId(resultSet.getInt("id"));
        return operationUserDto;
      });
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
  }


}

