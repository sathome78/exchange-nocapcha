package me.exrates.dao.impl;

import me.exrates.dao.TransferRequestDao;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Optional.of;


/**
 * created by ValkSam
 */

@Repository
public class TransferRequestDaoImpl implements TransferRequestDao {

  private static final Logger log = LogManager.getLogger("withdraw");

  protected static RowMapper<TransferRequestFlatDto> transferRequestFlatDtoRowMapper = (rs, idx) -> {
    TransferRequestFlatDto transferRequestFlatDto = new TransferRequestFlatDto();
    transferRequestFlatDto.setId(rs.getInt("id"));
    transferRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
    transferRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
    transferRequestFlatDto.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
    transferRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
    transferRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
    transferRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
    transferRequestFlatDto.setUserId(rs.getInt("user_id"));
    transferRequestFlatDto.setRecipientId(rs.getInt("recipient_id"));
    transferRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
    transferRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
    transferRequestFlatDto.setHash(rs.getString("hash"));
    return transferRequestFlatDto;
  };

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  private Optional<Integer> blockById(int id) {
    String sql = "SELECT COUNT(*) " +
        "FROM TRANSFER_REQUEST " +
        "WHERE TRANSFER_REQUEST.id = :id " +
        "FOR UPDATE ";
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
  }

  @Override
  public int create(TransferRequestCreateDto transferRequest) {
    final String sql = "INSERT INTO TRANSFER_REQUEST " +
        "(amount, commission, status_id," +
        " date_creation, status_modification_date, currency_id, merchant_id, user_id, recipient_user_id, commission_id, hash) " +
        "VALUES (:amount, :commission, :status_id, " +
        " NOW(), NOW(), :currency_id, :merchant_id, :user_id, :recipient_user_id, :commission_id, :hash)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("amount", transferRequest.getAmount())
        .addValue("commission", transferRequest.getCommission())
        .addValue("status_id", transferRequest.getStatusId())
        .addValue("currency_id", transferRequest.getCurrencyId())
        .addValue("merchant_id", transferRequest.getMerchantId())
        .addValue("user_id", transferRequest.getUserId())
        .addValue("recipient_user_id", transferRequest.getRecipientId())
        .addValue("commission_id", transferRequest.getCommissionId())
        .addValue("hash", transferRequest.getHash());
    jdbcTemplate.update(sql, params, keyHolder);
    return (int) keyHolder.getKey().longValue();
  }


  @Override
  public Optional<TransferRequestFlatDto> getFlatByIdAndBlock(int id) {
    blockById(id);
    return getFlatById(id);
  }

  @Override
  public Optional<TransferRequestFlatDto> getFlatById(int id) {
    String sql = "SELECT * " +
        " FROM WITHDRAW_REQUEST " +
        " WHERE id = :id";
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), transferRequestFlatDtoRowMapper));
  }

  @Override
  public void setStatusById(Integer id, InvoiceStatus newStatus) {
    final String sql = "UPDATE TRANSFER_REQUEST " +
        "  SET status_id = :new_status_id, " +
        "      status_modification_date = NOW() " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("new_status_id", newStatus.getCode());
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<TransferRequestFlatDto> findRequestsByStatusAndMerchant(Integer merchantId, List<Integer> statusId) {
    String sql = "SELECT TRANSFER_REQUEST.* " +
        " FROM TRANSFER_REQUEST " +
        " WHERE TRANSFER_REQUEST.merchant_id = :merchant_id  AND TRANSFER_REQUEST.status_id IN (:statuses)";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("statuses", statusId);
    }};
    return jdbcTemplate.query(sql, params, (rs, i) -> {
      return transferRequestFlatDtoRowMapper.mapRow(rs, i);
    });
  }

  @Override
  public void setHashById(Integer id, Map<String, String> params) {
    final String sql = "UPDATE TRANSFER_REQUEST " +
        "  SET hash = :hash " +
        "  WHERE id = :id";
    Map<String, Object> sqlParams = new HashMap<>();
    sqlParams.put("id", id);
    sqlParams.put("hash", params.get("hash"));
    jdbcTemplate.update(sql, sqlParams);
  }

}

