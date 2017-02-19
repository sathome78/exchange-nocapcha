package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.InvoiceRequestDao;
import me.exrates.model.*;
import me.exrates.model.dto.InvoiceUserDto;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;


/**
 * Created by ogolv on 26.07.2016.
 */
@Repository
@Log4j2
public class InvoiceRequestDaoImpl implements InvoiceRequestDao {

  @Autowired
  private NamedParameterJdbcTemplate parameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final static RowMapper<InvoiceRequest> invoiceRequestRowMapper = (resultSet, i) -> {
    InvoiceRequest invoiceRequest = new InvoiceRequest();
    Transaction transaction = TransactionDaoImpl.transactionRowMapper.mapRow(resultSet, i);
    invoiceRequest.setTransaction(transaction);
    invoiceRequest.setUserEmail(resultSet.getString("user_email"));
    invoiceRequest.setUserId(resultSet.getInt("user_id"));
    invoiceRequest.setAcceptanceUserEmail(resultSet.getString("acceptance_user_email"));
    invoiceRequest.setAcceptanceUserId(resultSet.getInt("acceptance_id"));
    Timestamp acceptanceTimeResult = resultSet.getTimestamp("acceptance_time");
    LocalDateTime acceptanceTime = acceptanceTimeResult == null ? null : acceptanceTimeResult.toLocalDateTime();
    invoiceRequest.setAcceptanceTime(acceptanceTime);
    Integer bankId = resultSet.getInt("bank_id");
    if (bankId != 0) {
      InvoiceBank invoiceBank = new InvoiceBank();
      invoiceBank.setId(bankId);
      invoiceBank.setName(resultSet.getString("bank_name"));
      invoiceBank.setAccountNumber(resultSet.getString("account_number"));
      invoiceBank.setRecipient(resultSet.getString("recipient"));
      invoiceRequest.setInvoiceBank(invoiceBank);
    }
    invoiceRequest.setPayerBankName(resultSet.getString("payer_bank_name"));
    invoiceRequest.setPayerBankCode(resultSet.getString("payer_bank_code"));
    invoiceRequest.setPayerAccount(resultSet.getString("payer_account"));
    invoiceRequest.setUserFullName(resultSet.getString("user_full_name"));
    invoiceRequest.setRemark(resultSet.getString("remark"));
    invoiceRequest.setReceiptScanPath(resultSet.getString("receipt_scan"));
    invoiceRequest.setInvoiceRequestStatus(InvoiceRequestStatusEnum.convert(resultSet.getInt("invoice_request_status_id")));
    invoiceRequest.setStatusUpdateDate(resultSet.getTimestamp("status_update_date").toLocalDateTime());
    return invoiceRequest;
  };

  private static final String SELECT_ALL = "SELECT inv.acceptance_time, user.id AS user_id, user.email AS user_email, " +
      "adm.id AS acceptance_id, adm.email AS acceptance_user_email, " +
      "TRANSACTION.id, TRANSACTION.amount, TRANSACTION.commission_amount, TRANSACTION.datetime, " +
      "                    TRANSACTION.operation_type_id,TRANSACTION.provided,TRANSACTION.confirmation, " +
      "                    TRANSACTION.source_id, TRANSACTION.source_type, WALLET.id, WALLET.active_balance, " +
      "                    WALLET.reserved_balance, WALLET.currency_id, COMPANY_WALLET.id, COMPANY_WALLET.balance, " +
      "                    COMPANY_WALLET.commission_balance, COMMISSION.id, COMMISSION.date, COMMISSION.value, " +
      "                    CURRENCY.id, CURRENCY.description, CURRENCY.name, MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
      "                    INVOICE_BANK.id AS bank_id, INVOICE_BANK.name AS bank_name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, " +
      "                    inv.user_full_name, inv.remark, inv.payer_bank_name,  inv.payer_bank_code, inv.payer_account, inv.receipt_scan, " +
      "                    inv.invoice_request_status_id, " +
      "                    inv.status_update_date " +
      "                    FROM INVOICE_REQUEST AS inv " +
      "    INNER JOIN TRANSACTION ON inv.transaction_id = TRANSACTION.id " +
      "    INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
      "    INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id " +
      "    INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id " +
      "    INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
      "    INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
      "    INNER JOIN USER AS user ON inv.user_id = user.id " +
      "    LEFT JOIN USER AS adm ON inv.acceptance_user_id = adm.id " +
      "    LEFT JOIN INVOICE_BANK ON inv.bank_id = INVOICE_BANK.id  ";


  @Override
  public void create(InvoiceRequest invoiceRequest, User user) {
    final String sql = "INSERT into INVOICE_REQUEST " +
        " (transaction_id, user_id, bank_id, user_full_name, remark, invoice_request_status_id, status_update_date) " +
        " values (:transaction_id, :user_id, :bank_id, :user_full_name, :remark, :invoice_request_status_id, NOW())";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("transaction_id", invoiceRequest.getTransaction().getId());
        put("user_id", user.getId());
        put("bank_id", invoiceRequest.getInvoiceBank().getId());
        put("user_full_name", invoiceRequest.getUserFullName());
        put("remark", invoiceRequest.getRemark());
        put("invoice_request_status_id",  invoiceRequest.getInvoiceRequestStatus().getCode());
      }
    };
    parameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void delete(InvoiceRequest invoiceRequest) {
    final String sql = "DELETE FROM INVOICE_REQUEST WHERE transaction_id = :id";
    final Map<String, Integer> params = singletonMap("id", invoiceRequest
        .getTransaction()
        .getId());
    parameterJdbcTemplate.update(sql, params);

  }

  @Override
  public void updateAcceptanceStatus(InvoiceRequest invoiceRequest) {
    final String sql = "UPDATE INVOICE_REQUEST" +
        " SET acceptance_user_id = (SELECT id FROM USER WHERE email=:email), acceptance_time = NOW(), " +
        " invoice_request_status_id = :invoice_request_status_id, " +
        " status_update_date = NOW() " +
        "WHERE transaction_id = :transaction_id";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("transaction_id", invoiceRequest.getTransaction().getId());
        put("email", invoiceRequest.getAcceptanceUserEmail());
        put("invoice_request_status_id", invoiceRequest.getInvoiceRequestStatus().getCode());
      }
    };
    parameterJdbcTemplate.update(sql, params);
  }

  @Override
  public Optional<InvoiceRequest> findById(int id) {
    final String sql = SELECT_ALL + " WHERE inv.transaction_id = :id";
    try {
      return of(parameterJdbcTemplate
          .queryForObject(sql,
              singletonMap("id", id),
              invoiceRequestRowMapper)
      );
    } catch (EmptyResultDataAccessException e) {
      return empty();
    }
  }

  @Override
  public Integer getStatusById(int id) {
    final String sql = "SELECT invoice_request_status_id " +
        " FROM INVOICE_REQUEST " +
        " WHERE transaction_id = :id";
    return parameterJdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class);
  }

  @Override
  public Optional<InvoiceRequest> findByIdAndBlock(int invoiceId) {
    final String sql =
        " SELECT COUNT(*) " +
            " FROM INVOICE_REQUEST AS inv " +
            " JOIN TRANSACTION ON inv.transaction_id = TRANSACTION.id " +
            " WHERE inv.transaction_id = :invoice_id " +
            " FOR UPDATE"; //FOR UPDATE Important!
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("invoice_id", invoiceId);
    }};
    parameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    return findById(invoiceId);
  }

  @Override
  public List<InvoiceRequest> findByStatus(List<Integer> invoiceRequestStatusIdList) {
    String sql = SELECT_ALL +
        " WHERE invoice_request_status_id IN (:invoice_request_status_id_list) ";
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("invoice_request_status_id_list", invoiceRequestStatusIdList);
    }};
    try {
      return parameterJdbcTemplate.query(sql, params, invoiceRequestRowMapper);
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
  }

  @Override
  public Optional<LocalDateTime> getAndBlockByIntervalAndStatus(Integer intervalMinutes, List<Integer> invoiceRequestStatusIdList) {
    LocalDateTime nowDate = jdbcTemplate.queryForObject("SELECT NOW()", LocalDateTime.class);
    String sql =
        " SELECT COUNT(*) " +
            " FROM INVOICE_REQUEST " +
            " WHERE status_update_date <= DATE_SUB(:now_date, INTERVAL " + intervalMinutes + " MINUTE) " +
            "       AND invoice_request_status_id IN (:invoice_request_status_id_list)" +
            " FOR UPDATE"; //FOR UPDATE Important!
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("now_date", nowDate);
      put("invoice_request_status_id_list", invoiceRequestStatusIdList);
    }};
    return Optional.ofNullable(parameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0 ? nowDate : null);
  }

  @Override
  public void setNewStatusByDateIntervalAndStatus(LocalDateTime nowDate, Integer intervalMinutes, Integer newInvoiceRequestStatusId, List<Integer> invoiceRequestStatusIdList) {
    final String sql =
        " UPDATE INVOICE_REQUEST " +
            " SET invoice_request_status_id = :invoice_request_status_id, " +
            "     status_update_date = :now_date " +
            " WHERE status_update_date <= DATE_SUB(:now_date, INTERVAL " + intervalMinutes + " MINUTE) " +
            "       AND invoice_request_status_id IN (:invoice_request_status_id_list)";
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("now_date", nowDate);
      put("invoice_request_status_id", newInvoiceRequestStatusId);
      put("invoice_request_status_id_list", invoiceRequestStatusIdList);
    }};
    parameterJdbcTemplate.update(sql, params);
  }

  @Override
  public List<InvoiceUserDto> findInvoicesListByStatusChangedAtDate(Integer invoiceRequestStatusId, LocalDateTime dateWhenChanged) {
    String sql =
        " SELECT transaction_id, user_id " +
            " FROM INVOICE_REQUEST " +
            " WHERE status_update_date = :date " +
            "       AND invoice_request_status_id = :invoice_request_status_id";
    final Map<String, Object> params = new HashMap<String, Object>() {{
      put("date", dateWhenChanged);
      put("invoice_request_status_id", invoiceRequestStatusId);
    }};
    try {
      return parameterJdbcTemplate.query(sql, params, (resultSet, i) -> {
        InvoiceUserDto invoiceUserDto = new InvoiceUserDto();
        invoiceUserDto.setUserId(resultSet.getInt("user_id"));
        invoiceUserDto.setInvoiceId(resultSet.getInt("transaction_id"));
        return invoiceUserDto;
      });
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
  }

  @Override
  public List<InvoiceRequest> findAll() {
    final String sql = SELECT_ALL + " ORDER BY acceptance_time IS NULL DESC, IF(acceptance_time IS NULL, TRANSACTION.datetime ,acceptance_time) DESC";
    return parameterJdbcTemplate.query(sql, invoiceRequestRowMapper);
  }

  @Override
  public List<InvoiceRequest> findAllForUser(String email) {
    String sql = SELECT_ALL + "WHERE user.email = :email";
    Map<String, String> params = Collections.singletonMap("email", email);
    try {
      return parameterJdbcTemplate.query(sql, params, invoiceRequestRowMapper);
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
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
  public List<ClientBank> findClientBanksForCurrency(Integer currencyId) {
    final String sql = "SELECT id, currency_id, name, code " +
        " FROM CLIENT_BANK " +
        " WHERE currency_id = :currency_id";
    final Map<String, Integer> params = Collections.singletonMap("currency_id", currencyId);
    return parameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
      ClientBank bank = new ClientBank();
      bank.setId(rs.getInt("id"));
      bank.setName(rs.getString("name"));
      bank.setCurrencyId(rs.getInt("currency_id"));
      bank.setCode(rs.getString("code"));
      return bank;
    });
  }


  @Override
  public InvoiceBank findBankById(Integer bankId) {
    final String sql = "SELECT id, currency_id, name, account_number, recipient " +
        " FROM INVOICE_BANK " +
        " WHERE id = :bank_id";
    final Map<String, Integer> params = Collections.singletonMap("bank_id", bankId);
    return parameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
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
  public void updateConfirmationInfo(InvoiceRequest invoiceRequest) {
    final String sql = "UPDATE INVOICE_REQUEST " +
        "  SET payer_bank_name = :payer_bank_name, payer_account = :payer_account, payer_bank_code = :payer_bank_code," +
        "      user_full_name = :user_full_name, remark = :remark, invoice_request_status_id = :invoice_request_status_id," +
        "      status_update_date = NOW() " +
        "  WHERE transaction_id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", invoiceRequest.getTransaction().getId());
    params.put("payer_bank_name", invoiceRequest.getPayerBankName());
    params.put("payer_bank_code", invoiceRequest.getPayerBankCode());
    params.put("payer_account", invoiceRequest.getPayerAccount());
    params.put("user_full_name", invoiceRequest.getUserFullName());
    params.put("remark", invoiceRequest.getRemark());
    params.put("invoice_request_status_id", ((InvoiceRequestStatusEnum) invoiceRequest.getInvoiceRequestStatus()).getCode());
    parameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void updateInvoiceRequestStatus(Integer invoiceRequestId, InvoiceRequestStatusEnum invoiceRequestStatus) {
    final String sql = "UPDATE INVOICE_REQUEST " +
        "  SET invoice_request_status_id = :invoice_request_status_id, " +
        "      status_update_date = NOW() " +
        "  WHERE transaction_id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", invoiceRequestId);
    params.put("invoice_request_status_id", invoiceRequestStatus.getCode());
    parameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void updateReceiptScan(Integer invoiceId, String receiptScanPath) {
    final String sql = "UPDATE INVOICE_REQUEST SET receipt_scan = :receipt_scan WHERE transaction_id = :id ";
    Map<String, Object> params = new HashMap<>();
    params.put("id", invoiceId);
    params.put("receipt_scan", receiptScanPath);
    parameterJdbcTemplate.update(sql, params);
  }
}
