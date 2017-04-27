package me.exrates.dao.impl;

import me.exrates.dao.RefillRequestDao;
import me.exrates.model.InvoiceBank;
import me.exrates.model.PagingData;
import me.exrates.model.dto.OperationUserDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatAdditionalDataDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Optional.of;
import static me.exrates.model.enums.TransactionSourceType.REFILL;


/**
 * created by ValkSam
 */

@Repository
public class RefillRequestDaoImpl implements RefillRequestDao {

  private static final Logger log = LogManager.getLogger("refill");

  protected static RowMapper<RefillRequestFlatDto> refillRequestFlatDtoRowMapper = (rs, idx) -> {
    RefillRequestFlatDto refillRequestFlatDto = new RefillRequestFlatDto();
    refillRequestFlatDto.setId(rs.getInt("id"));
    refillRequestFlatDto.setAddress(rs.getString("address"));
    refillRequestFlatDto.setUserId(rs.getInt("user_id"));
    refillRequestFlatDto.setPayerBankName(rs.getString("payer_bank_name"));
    refillRequestFlatDto.setPayerBankCode(rs.getString("payer_bank_code"));
    refillRequestFlatDto.setPayerAccount(rs.getString("payer_account"));
    refillRequestFlatDto.setRecipientBankAccount(rs.getString("payer_account"));
    refillRequestFlatDto.setUserFullName(rs.getString("user_full_name"));
    refillRequestFlatDto.setRemark(rs.getString("remark"));
    refillRequestFlatDto.setReceiptScan(rs.getString("receipt_scan"));
    refillRequestFlatDto.setReceiptScanName(rs.getString("receipt_scan_name"));
    refillRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
    refillRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
    refillRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
    refillRequestFlatDto.setStatus(RefillStatusEnum.convert(rs.getInt("status_id")));
    refillRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
    refillRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
    refillRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
    refillRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
    refillRequestFlatDto.setHash(rs.getString("hash"));
    refillRequestFlatDto.setMerchantTransactionId(rs.getString("merchant_transaction_id"));
    refillRequestFlatDto.setRecipientBankId(rs.getInt("recipient_bank_id"));
    refillRequestFlatDto.setRecipientBankName(rs.getString("name"));
    refillRequestFlatDto.setRecipientBankAccount(rs.getString("account_number"));
    refillRequestFlatDto.setRecipientBankRecipient(rs.getString("recipient"));
    refillRequestFlatDto.setAdminHolderId(rs.getInt("admin_holder_id"));
    return refillRequestFlatDto;
  };

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private MessageSource messageSource;

  private Optional<Integer> blockById(int id) {
    String sql = "SELECT COUNT(*) " +
        "FROM REFILL_REQUEST " +
        "WHERE REFILL_REQUEST.id = :id " +
        "FOR UPDATE ";
    return of(namedParameterJdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
  }

  @Override
  public Optional<Integer> findIdWithoutConfirmationsByMerchantIdAndCurrencyIdAndAddressAndStatusId(
      String address,
      Integer merchantId,
      Integer currencyId,
      List<Integer> statusList) {
    String sql = "SELECT RR.id " +
        " FROM REFILL_REQUEST RR " +
        " LEFT JOIN REFILL_REQUEST_CONFIRMATION RRC ON (RRC.refill_request_id = RR.id) " +
        " WHERE RR.address = :address " +
        "       AND RR.merchant_id = :merchant_id " +
        "       AND RR.currency_id = :currency_id " +
        "       AND RR.status_id IN (:status_id_list) " +
        "       AND RRC.id IS NULL ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("address", address);
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
      put("status_id_list", statusList);
    }};
    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<RefillRequestFlatDto> findAllWithoutConfirmationsByMerchantIdAndCurrencyIdAndStatusId(
      Integer merchantId,
      Integer currencyId,
      List<Integer> statusList) {
    String sql = "SELECT REFILL_REQUEST.*,  " +
        " INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient " +
        " FROM REFILL_REQUEST " +
        " LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = REFILL_REQUEST.recipient_bank_id) " +
        " LEFT JOIN REFILL_REQUEST_CONFIRMATION RRC ON (RRC.refill_request_id = REFILL_REQUEST.id) " +
        " WHERE REFILL_REQUEST.merchant_id = :merchant_id " +
        "       AND REFILL_REQUEST.currency_id = :currency_id " +
        "       AND REFILL_REQUEST.status_id IN (:status_id_list) " +
        "       AND RRC.id IS NULL ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
      put("status_id_list", statusList);
    }};
    return namedParameterJdbcTemplate.query(sql, params, refillRequestFlatDtoRowMapper);
  }

  @Override
  public List<RefillRequestFlatDto> findAllWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(
      Integer merchantId,
      Integer currencyId,
      List<InvoiceStatus> statusList) {
    String sql = "SELECT REFILL_REQUEST.*,  " +
        " INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient " +
        " FROM REFILL_REQUEST " +
        " LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = REFILL_REQUEST.recipient_bank_id) " +
        " JOIN REFILL_REQUEST_CONFIRMATION RRC ON (RRC.refill_request_id = REFILL_REQUEST.id) " +
        " WHERE REFILL_REQUEST.merchant_id = :merchant_id " +
        "       AND REFILL_REQUEST.currency_id = :currency_id " +
        "       AND REFILL_REQUEST.status_id IN (:status_id_list) " +
        "       AND RRC.id IS NULL ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
      put("status_id_list", statusList);
    }};
    return namedParameterJdbcTemplate.query(sql, params, refillRequestFlatDtoRowMapper);
  }

  @Override
  public Optional<Integer> findUserIdByMerchantIdAndCurrencyIdAndAddress(
      String address,
      Integer merchantId,
      Integer currencyId) {
    String sql = "SELECT RR.user_id " +
        " FROM REFILL_REQUEST RR " +
        " WHERE RR.address = :address " +
        "       AND RR.merchant_id = :merchant_id " +
        "       AND RR.currency_id = :currency_id " +
        " LIMIT 1 ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("address", address);
      put("merchant_id", merchantId);
      put("currency_id", currencyId);
    }};
    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public int create(RefillRequestCreateDto request) {
    final String sql = "INSERT INTO REFILL_REQUEST " +
        " (amount, status_id, currency_id, user_id, commission_id, merchant_id, " +
        "  recipient_bank_id, user_full_name, remark, address," +
        "  date_creation, status_modification_date) " +
        " VALUES " +
        " (:amount, :status_id, :currency_id, :user_id, :commission_id, :merchant_id, " +
        " :recipient_bank_id, :user_full_name, :remark, :address," +
        " NOW(), NOW())";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("amount", request.getAmount())
        .addValue("status_id", request.getStatus().getCode())
        .addValue("currency_id", request.getCurrencyId())
        .addValue("user_id", request.getUserId())
        .addValue("commission_id", request.getCommissionId())
        .addValue("merchant_id", request.getMerchantId())
        .addValue("recipient_bank_id", request.getRecipientBankId())
        .addValue("user_full_name", request.getUserFullName())
        .addValue("remark", request.getRemark())
        .addValue("address", request.getAddress());
    namedParameterJdbcTemplate.update(sql, params, keyHolder);
    return (int) keyHolder.getKey().longValue();
  }

  @Override
  public void setStatusById(Integer id, InvoiceStatus newStatus) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET status_id = :new_status_id, " +
        "      status_modification_date = NOW() " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("new_status_id", newStatus.getCode());
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setStatusAndConfirmationDataById(
      Integer id,
      InvoiceStatus newStatus,
      InvoiceConfirmData invoiceConfirmData) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET status_id = :new_status_id, " +
        "      status_modification_date = NOW(), " +
        "      payer_bank_code = :payer_bank_code, " +
        "      payer_bank_name = :payer_bank_name, " +
        "      payer_account = :payer_account, " +
        "      user_full_name = :user_full_name, " +
        "      remark = :remark, " +
        "      receipt_scan_name = :receipt_scan_name, " +
        "      receipt_scan = :receipt_scan " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("new_status_id", newStatus.getCode());
    params.put("payer_bank_code", invoiceConfirmData.getPayerBankCode());
    params.put("payer_bank_name", invoiceConfirmData.getPayerBankName());
    params.put("payer_account", invoiceConfirmData.getUserAccount());
    params.put("user_full_name", invoiceConfirmData.getUserFullName());
    params.put("remark", invoiceConfirmData.getRemark());
    params.put("receipt_scan_name", invoiceConfirmData.getReceiptScanName());
    params.put("receipt_scan", invoiceConfirmData.getReceiptScanPath());
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public List<InvoiceBank> findInvoiceBanksByCurrency(Integer currencyId) {
    final String sql = "SELECT id, currency_id, name, account_number, recipient " +
        " FROM INVOICE_BANK " +
        " WHERE currency_id = :currency_id";
    final Map<String, Integer> params = Collections.singletonMap("currency_id", currencyId);
    return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
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
    return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0 ? nowDate : null);
  }

  @Override
  public Optional<RefillRequestFlatDto> getFlatByIdAndBlock(Integer id) {
    blockById(id);
    return getFlatById(id);
  }

  @Override
  public Optional<RefillRequestFlatDto> getFlatById(Integer id) {
    String sql = "SELECT REFILL_REQUEST.*,  " +
        " INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient " +
        " FROM REFILL_REQUEST " +
        " LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = REFILL_REQUEST.recipient_bank_id) " +
        " WHERE REFILL_REQUEST.id = :id";
    return of(namedParameterJdbcTemplate.queryForObject(sql, singletonMap("id", id), refillRequestFlatDtoRowMapper));
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
    namedParameterJdbcTemplate.update(sql, params);
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
      return namedParameterJdbcTemplate.query(sql, params, (resultSet, i) -> {
        OperationUserDto operationUserDto = new OperationUserDto();
        operationUserDto.setUserId(resultSet.getInt("user_id"));
        operationUserDto.setId(resultSet.getInt("id"));
        return operationUserDto;
      });
    } catch (EmptyResultDataAccessException e) {
      return Collections.EMPTY_LIST;
    }
  }

  @Override
  public PagingData<List<RefillRequestFlatDto>> getPermittedFlatByStatus(
      List<Integer> statusIdList,
      Integer requesterUserId,
      DataTableParams dataTableParams,
      RefillFilterData refillFilterData) {
    final String JOINS_FOR_FILTER =
        " JOIN USER ON USER.id = REFILL_REQUEST.user_id ";
    String filter = refillFilterData.getSQLFilterClause();
    String sqlBase =
        " FROM REFILL_REQUEST " +
            " LEFT JOIN INVOICE_BANK IB ON (IB.id = REFILL_REQUEST.recipient_bank_id) " +
            " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
            "				(IOP.currency_id=REFILL_REQUEST.currency_id) " +
            "				AND (IOP.user_id=:requester_user_id) " +
            "				AND (IOP.operation_direction=:operation_direction) " +
            (filter.isEmpty() ? "" : JOINS_FOR_FILTER) +
            (statusIdList.isEmpty() ? "" : " WHERE status_id IN (:status_id_list) ");

    String whereClauseFilter = StringUtils.isEmpty(filter) ? "" : " AND ".concat(filter);
    String orderClause = dataTableParams.getOrderByClause();
    String offsetAndLimit = dataTableParams.getLimitAndOffsetClause();
    String sqlMain = String.join(" ", "SELECT REFILL_REQUEST.*, IB.*, IOP.invoice_operation_permission_id ",
        sqlBase, whereClauseFilter, orderClause, offsetAndLimit);
    String sqlCount = String.join(" ", "SELECT COUNT(*) ", sqlBase, whereClauseFilter);
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("status_id_list", statusIdList);
      put("requester_user_id", requesterUserId);
      put("operation_direction", "REFILL");
      put("offset", dataTableParams.getStart());
      put("limit", dataTableParams.getLength());
    }};
    params.putAll(refillFilterData.getNamedParams());

    List<RefillRequestFlatDto> requests = namedParameterJdbcTemplate.query(sqlMain, params, (rs, i) -> {
      RefillRequestFlatDto refillRequestFlatDto = refillRequestFlatDtoRowMapper.mapRow(rs, i);
      refillRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
      return refillRequestFlatDto;
    });
    Integer totalQuantity = namedParameterJdbcTemplate.queryForObject(sqlCount, params, Integer.class);
    PagingData<List<RefillRequestFlatDto>> result = new PagingData<>();
    result.setData(requests);
    result.setFiltered(totalQuantity);
    result.setTotal(totalQuantity);
    return result;
  }

  @Override
  public RefillRequestFlatDto getPermittedFlatById(
      Integer id,
      Integer requesterUserId) {
    String sql = "SELECT REFILL_REQUEST.*, IOP.invoice_operation_permission_id " +
        " FROM REFILL_REQUEST " +
        " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
        "				(IOP.currency_id=REFILL_REQUEST.currency_id) " +
        "				AND (IOP.user_id=:requester_user_id) " +
        "				AND (IOP.operation_direction=:operation_direction) " +
        " WHERE REFILL_REQUEST.id=:id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("id", id);
      put("requester_user_id", requesterUserId);
      put("operation_direction", "REFILL");
    }};
    return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
      RefillRequestFlatDto refillRequestFlatDto = refillRequestFlatDtoRowMapper.mapRow(rs, i);
      refillRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
      return refillRequestFlatDto;
    });
  }

  @Override
  public RefillRequestFlatAdditionalDataDto getAdditionalDataForId(int id) {
    String sql = "SELECT " +
        "   CUR.name AS currency_name, " +
        "   USER.email AS user_email, " +
        "   ADMIN.email AS admin_email, " +
        "   M.name AS merchant_name, " +
        "   TX.amount AS amount, TX.commission_amount AS commission_amount, " +
        "   (SELECT IF(MAX(confirmation_number) IS NULL, -1, MAX(confirmation_number)) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = :id) AS confirmations, " +
        "   (SELECT amount FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = :id ORDER BY id DESC LIMIT 1) AS amount_by_bch " +
        " FROM REFILL_REQUEST WR " +
        " JOIN CURRENCY CUR ON (CUR.id = WR.currency_id) " +
        " JOIN USER USER ON (USER.id = WR.user_id) " +
        " LEFT JOIN USER ADMIN ON (ADMIN.id = WR.admin_holder_id) " +
        " JOIN MERCHANT M ON (M.id = WR.merchant_id) " +
        " LEFT JOIN TRANSACTION TX ON (TX.source_type = :source_type) AND (TX.source_id = :id) " +
        " WHERE WR.id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("id", id)
        .addValue("source_type", REFILL.name());
    return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, idx) -> {
          RefillRequestFlatAdditionalDataDto refillRequestFlatAdditionalDataDto = new RefillRequestFlatAdditionalDataDto();
          refillRequestFlatAdditionalDataDto.setUserEmail(rs.getString("user_email"));
          refillRequestFlatAdditionalDataDto.setAdminHolderEmail(rs.getString("admin_email"));
          refillRequestFlatAdditionalDataDto.setCurrencyName(rs.getString("currency_name"));
          refillRequestFlatAdditionalDataDto.setMerchantName(rs.getString("merchant_name"));
          refillRequestFlatAdditionalDataDto.setCommissionAmount(rs.getBigDecimal("commission_amount"));
          refillRequestFlatAdditionalDataDto.setTransactionAmount(rs.getBigDecimal("amount"));
          refillRequestFlatAdditionalDataDto.setByBchAmount(rs.getBigDecimal("amount_by_bch"));
          refillRequestFlatAdditionalDataDto.setConfirmations(rs.getInt("confirmations"));
          return refillRequestFlatAdditionalDataDto;
        }
    );
  }

  @Override
  public void setHolderById(Integer id, Integer holderId) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET admin_holder_id = :admin_holder_id " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("admin_holder_id", holderId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setRemarkById(Integer id, String remark) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET remark = :remark " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("remark", remark);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setMerchantTransactionIdById(Integer id, String merchantTransactionId) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET merchant_transaction_id = :merchant_transaction_id " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("merchant_transaction_id", merchantTransactionId);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public void setHashById(Integer id, String hash) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET hash = :hash " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("hash", hash);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public boolean checkInputRequests(int currencyId, String email) {
    String sql = "SELECT " +
        " (SELECT COUNT(*) FROM WITHDRAW_REQUEST REQUEST " +
        " JOIN USER ON(USER.id = REQUEST.user_id) " +
        " WHERE USER.email = :email and REQUEST.currency_id = currency_id " +
        " and DATE(REQUEST.date_creation) = CURDATE()) <  " +
        " " +
        "(SELECT CURRENCY_LIMIT.max_daily_request FROM CURRENCY_LIMIT  " +
        " JOIN USER ON (USER.roleid = CURRENCY_LIMIT.user_role_id) " +
        " WHERE USER.email = :email AND operation_type_id = 1 AND currency_id = :currency_id) ;";
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("currency_id", currencyId);
    params.put("email", email);
    return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) == 1;
  }

  @Override
  public void setAddressById(
      Integer id,
      String address) {
    final String sql = "UPDATE REFILL_REQUEST " +
        "  SET address = :address " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("address", address);
    namedParameterJdbcTemplate.update(sql, params);
  }

  @Override
  public Integer findConfirmationsNumberByRequestId(Integer requestId) {
    String sql = "SELECT IF(MAX(confirmation_number) IS NULL, -1, MAX(confirmation_number)) " +
        "  FROM REFILL_REQUEST_CONFIRMATION RRC " +
        "  WHERE RRC.refill_request_id = refill_request_id ";
    return namedParameterJdbcTemplate.queryForObject(sql, singletonMap("refill_request_id", requestId), Integer.class);
  }

  @Override
  public void setConfirmationsNumberByRequestId(Integer requestId, BigDecimal amount, Integer confirmations) {
    String sql = " INSERT INTO REFILL_REQUEST_CONFIRMATION " +
        "  (refill_request_id, datetime, confirmation_number, amount) " +
        "  VALUES (:request_id, NOW(), :confirmation_number, :amount) ";
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("request_id", requestId)
        .addValue("confirmation_number", confirmations)
        .addValue("amount", amount);
    namedParameterJdbcTemplate.update(sql, params);
  }


}

