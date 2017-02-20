package me.exrates.dao.impl;

import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.dto.PendingPaymentSimpleDto;
import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class PendingPaymentDaoImpl implements PendingPaymentDao {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  private final static RowMapper<PendingPayment> pendingPaymentRowMapper = (resultSet, i) -> {
    PendingPayment pendingPayment = new PendingPayment();
    Transaction transaction = TransactionDaoImpl.transactionRowMapper.mapRow(resultSet, i);
    pendingPayment.setTransaction(transaction);
    pendingPayment.setInvoiceId(resultSet.getInt("invoice_id"));
    pendingPayment.setUserEmail(resultSet.getString("user_email"));
    pendingPayment.setUserId(resultSet.getInt("user_id"));
    pendingPayment.setAcceptanceUserEmail(resultSet.getString("acceptance_user_email"));
    pendingPayment.setAcceptanceUserId(resultSet.getInt("acceptance_id"));
    pendingPayment.setHash(resultSet.getString("hash"));
    Timestamp acceptanceTimeResult = resultSet.getTimestamp("acceptance_time");
    LocalDateTime acceptanceTime = acceptanceTimeResult == null ? null : acceptanceTimeResult.toLocalDateTime();
    pendingPayment.setAcceptanceTime(acceptanceTime);
    pendingPayment.setPendingPaymentStatus(PendingPaymentStatusEnum.convert(resultSet.getInt("pending_payment_status_id")));
    pendingPayment.setStatusUpdateDate(resultSet.getTimestamp("status_update_date").toLocalDateTime());
    return pendingPayment;
  };

  private static final String SELECT_ALL = "SELECT " +
      " PP.invoice_id, PP.transaction_hash, PP.address, PP.hash, PP.pending_payment_status_id, PP.status_update_date, PP.acceptance_time, " +
      " user.id AS user_id, user.email AS user_email, " +
      " adm.id AS acceptance_id, adm.email AS acceptance_user_email, " +
      " TRANSACTION.id, TRANSACTION.amount, TRANSACTION.commission_amount, TRANSACTION.datetime, " +
      "                    TRANSACTION.operation_type_id,TRANSACTION.provided,TRANSACTION.confirmation, " +
      "                    TRANSACTION.source_id, TRANSACTION.source_type, WALLET.id, WALLET.active_balance, " +
      "                    WALLET.reserved_balance, WALLET.currency_id, COMPANY_WALLET.id, COMPANY_WALLET.balance, " +
      "                    COMPANY_WALLET.commission_balance, COMMISSION.id, COMMISSION.date, COMMISSION.value, " +
      "                    CURRENCY.id, CURRENCY.description, CURRENCY.name, MERCHANT.id,MERCHANT.name,MERCHANT.description " +
      "    FROM PENDING_PAYMENT AS PP " +
      "    INNER JOIN TRANSACTION ON PP.invoice_id = TRANSACTION.id " +
      "    INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
      "    INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id " +
      "    INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id " +
      "    INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
      "    INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
      "    INNER JOIN USER AS user ON user.id = WALLET.user_id " +
      "    LEFT JOIN USER AS adm ON PP.acceptance_user_id = adm.id ";

  @Override
  public void create(final PendingPayment pendingPayment) {
    final String sql = "INSERT INTO PENDING_PAYMENT " +
        " (invoice_id, transaction_hash, address, pending_payment_status_id, status_update_date) " +
        " VALUES (:invoiceId, :transactionHash,:address, :pending_payment_status_id, NOW())";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("invoiceId", pendingPayment.getInvoiceId());
        put("transactionHash", pendingPayment.getTransactionHash());
        put("address", pendingPayment.getAddress());
        put("pending_payment_status_id", pendingPayment.getPendingPaymentStatus().getCode());
      }
    };
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<PendingPaymentSimpleDto> findAllByHash(String hash) {
    final String sql = "SELECT * FROM PENDING_PAYMENT WHERE transaction_hash = :hash";
    final Map<String, String> params = Collections.singletonMap("hash", hash);
    return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(PendingPaymentSimpleDto.class));
  }

  @Override
  public Optional<PendingPayment> findByInvoiceId(Integer invoiceId) {
    final String sql = SELECT_ALL +
        " WHERE invoice_id = :invoice_id";
    final Map<String, Integer> params = Collections.singletonMap("invoiceId", invoiceId);
    try {
      return of(jdbcTemplate
          .queryForObject(sql,
              singletonMap("invoice_id", invoiceId),
              pendingPaymentRowMapper)
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public void delete(final int invoiceId) {
    final String sql = "DELETE FROM PENDING_PAYMENT WHERE invoice_id = :invoiceId";
    final Map<String, Integer> params = Collections.singletonMap("invoiceId", invoiceId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public Optional<PendingPayment> findByIdAndBlock(Integer invoiceId) {
    final String sql =
        " SELECT COUNT(*) " +
            " FROM PENDING_PAYMENT AS pp " +
            " JOIN TRANSACTION ON pp.invoice_id = TRANSACTION.id " +
            " WHERE pp.invoice_id = :invoice_id " +
            " FOR UPDATE"; //FOR UPDATE Important!
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("invoice_id", invoiceId);
    }};
    jdbcTemplate.queryForObject(sql, params, Integer.class);
    return findByInvoiceId(invoiceId);
  }

  @Override
  public void updateAcceptanceStatus(PendingPayment pendingPayment) {
    final String sql = "UPDATE PENDING_PAYMENT " +
        " SET acceptance_user_id = (SELECT id FROM USER WHERE email=:email), " +
        "     acceptance_time = NOW(), " +
        "     pending_payment_status_id = :pending_payment_status_id, " +
        "     hash = :hash, " +
        "     status_update_date = NOW() " +
        "WHERE invoice_id = :invoice_id";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("invoice_id", pendingPayment.getTransaction().getId());
        put("email", pendingPayment.getAcceptanceUserEmail());
        put("hash", pendingPayment.getHash());
        put("pending_payment_status_id", pendingPayment.getPendingPaymentStatus().getCode());
      }
    };
    jdbcTemplate.update(sql, params);
  }

  @Override
  public void setStatusById(Integer invoiceId, Integer newStatus) {
    final String sql = "UPDATE PENDING_PAYMENT " +
        " SET pending_payment_status_id = :pending_payment_status_id, " +
        "     status_update_date = NOW() " +
        " WHERE invoice_id = :invoice_id ";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("invoice_id", invoiceId);
        put("pending_payment_status_id", newStatus);
      }
    };
    jdbcTemplate.update(sql, params);
  }

  @Override
  @Transactional
  public Optional<PendingPaymentStatusDto> setStatusAndHashByAddressAndStatus(String address, Integer currentStatus, Integer newStatus, String hash) {
    PendingPaymentStatusDto pendingPayment = getStatusForAddressAndStatusAndBlock(address, currentStatus);
    if (pendingPayment == null) {
      return empty();
    }
    final String sql = "UPDATE PENDING_PAYMENT " +
        " SET pending_payment_status_id = :pending_payment_status_id, " +
        "     status_update_date = NOW(), " +
        "     hash = :hash " +
        " WHERE invoice_id = :invoice_id ";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("invoice_id", pendingPayment.getInvoiceId());
        put("pending_payment_status_id", newStatus);
        put("hash", hash);
      }
    };
    jdbcTemplate.update(sql, params);
    pendingPayment.setPendingPaymentStatus(PendingPaymentStatusEnum.convert(newStatus));
    return of(pendingPayment);
  }

  private PendingPaymentStatusDto getStatusForAddressAndStatusAndBlock(String address, Integer statusId) {
    String sql = "SELECT invoice_id, pending_payment_status_id " +
        " FROM PENDING_PAYMENT " +
        " WHERE address= :address " +
        "       AND pending_payment_status_id = :pending_payment_status_id " +
        " FOR UPDATE "; //FOR UPDATE is important
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("address", address);
      put("pending_payment_status_id", statusId);
    }};
    try {
      return jdbcTemplate.queryForObject(sql, params, (rs, i) -> {
        PendingPaymentStatusDto result = new PendingPaymentStatusDto();
        result.setInvoiceId(rs.getInt("invoice_id"));
        result.setPendingPaymentStatus(PendingPaymentStatusEnum.convert(rs.getInt("pending_payment_status_id")));
        return result;
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public Optional<PendingPaymentSimpleDto> findByAddressAndNotProvided(String address) {
    String sql = "SELECT PP.* " +
        " FROM PENDING_PAYMENT PP " +
        " JOIN TRANSACTION TX ON (TX.id = PP.invoice_id) AND (TX.provided = 0)" +
        " WHERE address= :address ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("address", address);
    }};
    try {
      return of(jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(PendingPaymentSimpleDto.class)));
    } catch (EmptyResultDataAccessException e) {
      return empty();
    }
  }

  @Override
  public boolean existsPendingPaymentWithAddressAndStatus(String address, List<Integer> paymentStatusIdList) {
    String sql = "SELECT COUNT(*) " +
        " FROM PENDING_PAYMENT " +
        " WHERE address= :address " +
        "       AND pending_payment_status_id IN (:pending_payment_status_id_list) ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("address", address);
      put("pending_payment_status_id_list", paymentStatusIdList);
    }};
    return jdbcTemplate.queryForObject(sql, params, Integer.class) != 0;
  }
}