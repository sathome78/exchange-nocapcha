package me.exrates.dao.impl;

import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.ClientBank;
import me.exrates.model.MerchantImage;
import me.exrates.model.PagingData;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.lang3.StringUtils;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.Optional.of;
import static me.exrates.model.enums.TransactionSourceType.WITHDRAW;


/**
 * created by ValkSam
 */

@Repository
public class WithdrawRequestDaoImpl implements WithdrawRequestDao {

  private static final Logger log = LogManager.getLogger("withdraw");

  protected static RowMapper<WithdrawRequestFlatDto> withdrawRequestFlatDtoRowMapper = (rs, idx) -> {
    WithdrawRequestFlatDto withdrawRequestFlatDto = new WithdrawRequestFlatDto();
    withdrawRequestFlatDto.setId(rs.getInt("id"));
    withdrawRequestFlatDto.setWallet(rs.getString("wallet"));
    withdrawRequestFlatDto.setUserId(rs.getInt("user_id"));
    withdrawRequestFlatDto.setMerchantImageId(rs.getInt("merchant_image_id"));
    withdrawRequestFlatDto.setRecipientBankName(rs.getString("recipient_bank_name"));
    withdrawRequestFlatDto.setRecipientBankCode(rs.getString("recipient_bank_code"));
    withdrawRequestFlatDto.setUserFullName(rs.getString("user_full_name"));
    withdrawRequestFlatDto.setRemark(rs.getString("remark"));
    withdrawRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
    withdrawRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
    withdrawRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
    withdrawRequestFlatDto.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
    withdrawRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
    withdrawRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
    withdrawRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
    withdrawRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
    withdrawRequestFlatDto.setAdminHolderId(rs.getInt("admin_holder_id"));
    return withdrawRequestFlatDto;
  };

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  private MessageSource messageSource;

  private Optional<Integer> blockById(int id) {
    String sql = "SELECT COUNT(*) " +
        "FROM WITHDRAW_REQUEST " +
        "WHERE WITHDRAW_REQUEST.id = :id " +
        "FOR UPDATE ";
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
  }

  @Override
  public List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList) {
    String sql = "SELECT WR.*, " +
        "         USER.email AS user_email, USER.nickname AS nickname, " +
        "         ADM.email AS admin_email, " +
        "         MERCHANT.name AS merchant_name, " +
        "         CURRENCY.name AS currency_name" +
        " FROM WITHDRAW_REQUEST WR " +
        " JOIN CURRENCY ON CURRENCY.id = WR.currency_id " +
        " JOIN MERCHANT ON MERCHANT.id = WR.merchant_id " +
        " JOIN USER AS USER ON USER.id = WR.user_id " +
        " LEFT JOIN USER AS ADM ON ADM.id = WR.admin_holder_id " +
        " WHERE " +
        "    WR.date_creation BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s') " +
        "    AND (WR.currency_id IN (:currency_list)) " +
        (roleIdList.isEmpty() ? "" : " AND USER.roleid IN (:role_id_list)");
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("start_date", startDate);
      put("end_date", endDate);
      if (!roleIdList.isEmpty()) {
        put("role_id_list", roleIdList);
      }
      put("currency_list", currencyList);
    }};
    return jdbcTemplate.query(sql, params, new RowMapper<WithdrawRequestFlatForReportDto>() {
      @Override
      public WithdrawRequestFlatForReportDto mapRow(ResultSet rs, int i) throws SQLException {
        WithdrawRequestFlatForReportDto withdrawRequestFlatForReportDto = new WithdrawRequestFlatForReportDto();
        withdrawRequestFlatForReportDto.setInvoiceId(rs.getInt("WR.id"));
        withdrawRequestFlatForReportDto.setWallet(rs.getString("wallet"));
        withdrawRequestFlatForReportDto.setRecipientBank(rs.getString("recipient_bank_name"));
        withdrawRequestFlatForReportDto.setAdminEmail(rs.getString("admin_email"));
        withdrawRequestFlatForReportDto.setAcceptanceTime(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
        withdrawRequestFlatForReportDto.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
        withdrawRequestFlatForReportDto.setUserFullName(rs.getString("user_full_name"));
        withdrawRequestFlatForReportDto.setUserNickname(rs.getString("nickname"));
        withdrawRequestFlatForReportDto.setUserEmail(rs.getString("user_email"));
        withdrawRequestFlatForReportDto.setAmount(rs.getBigDecimal("amount"));
        withdrawRequestFlatForReportDto.setCommissionAmount(rs.getBigDecimal("commission"));
        withdrawRequestFlatForReportDto.setDatetime(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
        withdrawRequestFlatForReportDto.setCurrency(rs.getString("currency_name"));
        withdrawRequestFlatForReportDto.setSourceType(WITHDRAW);
        withdrawRequestFlatForReportDto.setMerchant(rs.getString("merchant_name"));
        return withdrawRequestFlatForReportDto;
      }
    });
  }

  @Override
  public Integer findStatusIdByRequestId(Integer withdrawRequestId) {
    String sql = "SELECT status FROM WITHDRAW_REQUEST WHERE transaction_id = :request_id";
    return jdbcTemplate.queryForObject(sql, Collections.singletonMap("request_id", withdrawRequestId), Integer.class);
  }

  @Override
  public int create(WithdrawRequestCreateDto withdrawRequest) {
    final String sql = "INSERT INTO WITHDRAW_REQUEST " +
        "(wallet, merchant_image_id, recipient_bank_name, recipient_bank_code, user_full_name, remark, amount, commission, status_id," +
        " date_creation, status_modification_date, currency_id, merchant_id, user_id, commission_id) " +
        "VALUES (:wallet, :merchant_image_id, :payer_bank_name, :payer_bank_code, :user_full_name, :remark, :amount, :commission, :status_id," +
        " NOW(), NOW(), :currency_id, :merchant_id, :user_id, :commission_id)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("wallet", withdrawRequest.getDestinationWallet())
        .addValue("merchant_image_id", withdrawRequest.getMerchantImage().getId() == 0 ? null : withdrawRequest.getMerchantImage().getId())
        .addValue("payer_bank_name", withdrawRequest.getRecipientBankName())
        .addValue("payer_bank_code", withdrawRequest.getRecipientBankCode())
        .addValue("user_full_name", withdrawRequest.getUserFullName())
        .addValue("remark", withdrawRequest.getRemark())
        .addValue("amount", withdrawRequest.getAmount())
        .addValue("commission", withdrawRequest.getCommission())
        .addValue("status_id", withdrawRequest.getStatusId())
        .addValue("currency_id", withdrawRequest.getCurrencyId())
        .addValue("merchant_id", withdrawRequest.getMerchantId())
        .addValue("user_id", withdrawRequest.getUserId())
        .addValue("commission_id", withdrawRequest.getCommissionId());
    jdbcTemplate.update(sql, params, keyHolder);
    return (int) keyHolder.getKey().longValue();
  }

  @Override
  public List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
      String email,
      Integer offset,
      Integer limit,
      List<Integer> operationTypeIdList,
      Locale locale) {
    String sql = " SELECT " +
        "    IF (WITHDRAW_REQUEST.date_creation IS NOT NULL, WITHDRAW_REQUEST.date_creation, TRANSACTION.datetime) AS datetime, " +
        "    CURRENCY.name as currency, TRANSACTION.amount, TRANSACTION.commission_amount, " +
        "    TRANSACTION.source_type, TRANSACTION.source_id AS source_id, TRANSACTION.confirmation, " +
        "    case when OPERATION_TYPE.name = 'input' or WITHDRAW_REQUEST.merchant_image_id is null  \n" +
        "              then MERCHANT.name  \n" +
        "              else MERCHANT_IMAGE.image_name end as merchant,  " +
        "    OPERATION_TYPE.name as operation_type, TRANSACTION.id AS id, TRANSACTION.provided, " +
        "    INVOICE_BANK.account_number AS bank_account, " +
        "    USER.id AS user_id," +
        "    INVOICE_REQUEST.invoice_request_status_id, " +
        "    INVOICE_REQUEST.status_update_date AS invoice_request_status_update_date," +
        "    INVOICE_REQUEST.user_full_name, INVOICE_REQUEST.remark, " +
        "    PENDING_PAYMENT.pending_payment_status_id, " +
        "    PENDING_PAYMENT.status_update_date AS pending_payment_status_update_date," +
        "    WITHDRAW_REQUEST.status_id AS withdraw_request_status_id, " +
        "    WITHDRAW_REQUEST.status_modification_date AS withdraw_request_status_update_date, " +
        "    WITHDRAW_REQUEST.admin_holder_id AS admin_holder_id," +
            "WITHDRAW_REQUEST.wallet AS withdraw_recipient_account" +
        "  FROM TRANSACTION " +
        "    left join CURRENCY on TRANSACTION.currency_id=CURRENCY.id" +
        "    left join INVOICE_REQUEST on TRANSACTION.id=INVOICE_REQUEST.transaction_id" +
        "    left join PENDING_PAYMENT on TRANSACTION.id=PENDING_PAYMENT.invoice_id" +
        "    left join WITHDRAW_REQUEST on TRANSACTION.source_type = 'WITHDRAW' AND TRANSACTION.source_id=WITHDRAW_REQUEST.id" +
        "    left join INVOICE_BANK on INVOICE_REQUEST.bank_id = INVOICE_BANK.id " +
        "    left join MERCHANT on TRANSACTION.merchant_id = MERCHANT.id " +
        "    left join MERCHANT_IMAGE on WITHDRAW_REQUEST.merchant_image_id=MERCHANT_IMAGE.id " +
        "    left join OPERATION_TYPE on TRANSACTION.operation_type_id=OPERATION_TYPE.id" +
        "    left join WALLET on TRANSACTION.user_wallet_id=WALLET.id" +
        "    left join USER on WALLET.user_id=USER.id" +
        "  WHERE " +
        "    TRANSACTION.operation_type_id IN (:operation_type_id_list) and " +
        "    USER.email=:email " +
        "  UNION " +
        "  (SELECT " +
        "     WR.date_creation, " +
        "     CUR.name, WR.amount, WR.commission, " +
        "     'WITHDRAW', WR.id, -1," +
        "     IF(WR.merchant_image_id IS NULL, M.name, MI.image_name), " +
        "     'Output', WR.id, 1, " +
        "     null, " +
        "     USER.id, " +
        "     null, " +
        "     WR.status_modification_date, " +
        "     WR.user_full_name, WR.remark, " +
        "     null, " +
        "     null, " +
        "     WR.status_id, " +
        "     WR.status_modification_date, " +
        "     WR.admin_holder_id," +
        "     WR.wallet " +
        "   FROM WITHDRAW_REQUEST WR " +
        "     JOIN CURRENCY CUR ON CUR.id=WR.currency_id " +
        "     JOIN USER USER ON USER.id=WR.user_id " +
        "     JOIN MERCHANT M ON M.id=WR.merchant_id " +
        "     LEFT JOIN MERCHANT_IMAGE MI ON MI.id=WR.merchant_image_id " +
        "   WHERE USER.email=:email AND " +
        "     NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type='WITHDRAW' AND TX.source_id=WR.id AND TX.operation_type_id=2) " +
        "  )  " +
        "  ORDER BY datetime DESC, id DESC " +
        (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
    final Map<String, Object> params = new HashMap<>();
    params.put("email", email);
    params.put("operation_type_id_list", operationTypeIdList);
    return jdbcTemplate.query(sql, params, (rs, i) -> {
      MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
      myInputOutputHistoryDto.setDatetime(rs.getTimestamp("datetime").toLocalDateTime());
      myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
      myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
      myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
      myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
      myInputOutputHistoryDto.setOperationType(rs.getString("operation_type"));
      myInputOutputHistoryDto.setId(rs.getInt("id"));
      myInputOutputHistoryDto.setProvided(rs.getInt("provided"));
      myInputOutputHistoryDto.setTransactionProvided(rs.getInt("provided") == 0 ?
          messageSource.getMessage("inputoutput.statusFalse", null, locale) :
          messageSource.getMessage("inputoutput.statusTrue", null, locale));
      myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
      String bankAccount = rs.getString("bank_account") == null ? rs.getString("withdraw_recipient_account") :
              rs.getString("bank_account");
      myInputOutputHistoryDto.setBankAccount(bankAccount);
      TransactionSourceType sourceType = TransactionSourceType.convert(rs.getString("source_type"));
      myInputOutputHistoryDto.setSourceType(sourceType);
      Stream.of(rs.getObject("invoice_request_status_id"),
          rs.getObject("pending_payment_status_id"),
          rs.getObject("withdraw_request_status_id"))
          .filter(Objects::nonNull)
          .findFirst()
          .ifPresent(obj -> myInputOutputHistoryDto.setStatus((Integer) obj));
      InvoiceStatus status = myInputOutputHistoryDto.getStatus();
      Stream.of(rs.getTimestamp("invoice_request_status_update_date"),
          rs.getTimestamp("pending_payment_status_update_date"),
          rs.getTimestamp("withdraw_request_status_update_date"))
          .filter(Objects::nonNull)
          .findFirst()
          .ifPresent(obj -> myInputOutputHistoryDto.setStatusUpdateDate(obj.toLocalDateTime()));
      /**/
      myInputOutputHistoryDto.setUserFullName(rs.getString("user_full_name"));
      myInputOutputHistoryDto.setRemark(rs.getString("remark"));
      myInputOutputHistoryDto.setConfirmation(rs.getInt("confirmation"));
      myInputOutputHistoryDto.setAdminHolderId(rs.getInt("admin_holder_id"));
      myInputOutputHistoryDto.setSourceId(rs.getInt("source_id"));
      log.debug(String.format("id: %s, status: %s, source: %s, optype: %s", myInputOutputHistoryDto.getId(), myInputOutputHistoryDto.getStatus(),
          myInputOutputHistoryDto.getSourceType(), myInputOutputHistoryDto.getOperationType()));
      return myInputOutputHistoryDto;
    });
  }

  @Override
  public void setStatusById(Integer id, InvoiceStatus newStatus) {
    final String sql = "UPDATE WITHDRAW_REQUEST " +
        "  SET status_id = :new_status_id, " +
        "      status_modification_date = NOW() " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("new_status_id", newStatus.getCode());
    jdbcTemplate.update(sql, params);
  }

  @Override
  public Optional<WithdrawRequestFlatDto> getFlatByIdAndBlock(int id) {
    blockById(id);
    return getFlatById(id);
  }

  @Override
  public Optional<WithdrawRequestFlatDto> getFlatById(int id) {
    String sql = "SELECT * " +
        " FROM WITHDRAW_REQUEST " +
        " WHERE id = :id";
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), withdrawRequestFlatDtoRowMapper));
  }

  @Override
  public PagingData<List<WithdrawRequestFlatDto>> getPermittedFlatByStatus(
      List<Integer> statusIdList,
      Integer requesterUserId,
      DataTableParams dataTableParams,
      WithdrawFilterData withdrawFilterData) {
    final String JOINS_FOR_FILTER =
        " JOIN USER ON USER.id = WITHDRAW_REQUEST.user_id ";
    String filter = withdrawFilterData.getSQLFilterClause();
    String sqlBase =
        " FROM WITHDRAW_REQUEST " +
            " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
            "				(IOP.currency_id=WITHDRAW_REQUEST.currency_id) " +
            "				AND (IOP.user_id=:requester_user_id) " +
            "				AND (IOP.operation_direction=:operation_direction) " +
            (filter.isEmpty() ? "": JOINS_FOR_FILTER)+
            (statusIdList.isEmpty() ? "" : " WHERE status_id IN (:status_id_list) ");

    String whereClauseFilter = StringUtils.isEmpty(filter) ? "" : " AND ".concat(filter);
    String orderClause = dataTableParams.getOrderByClause();
    String offsetAndLimit = " LIMIT :limit OFFSET :offset ";
    String sqlMain = new StringJoiner(" ")
        .add("SELECT WITHDRAW_REQUEST.*, IOP.invoice_operation_permission_id ")
        .add(sqlBase)
        .add(whereClauseFilter)
        .add(orderClause)
        .add(offsetAndLimit)
        .toString();

    String sqlCount = new StringJoiner(" ")
        .add("SELECT COUNT(*) ")
        .add(sqlBase)
        .add(whereClauseFilter)
        .toString();

    Map<String, Object> params = new HashMap<String, Object>() {{
      put("status_id_list", statusIdList);
      put("requester_user_id", requesterUserId);
      put("operation_direction", "WITHDRAW");
      put("offset", dataTableParams.getStart());
      put("limit", dataTableParams.getLength());
    }};
    params.putAll(withdrawFilterData.getNamedParams());

    List<WithdrawRequestFlatDto> requests = jdbcTemplate.query(sqlMain, params, (rs, i) -> {
      WithdrawRequestFlatDto withdrawRequestFlatDto = withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
      withdrawRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
      return withdrawRequestFlatDto;
    });
    Integer totalQuantity = jdbcTemplate.queryForObject(sqlCount, params, Integer.class);
    PagingData<List<WithdrawRequestFlatDto>> result = new PagingData<>();
    result.setData(requests);
    result.setFiltered(totalQuantity);
    result.setTotal(totalQuantity);
    return result;
  }

  @Override
  public WithdrawRequestFlatDto getPermittedFlatById(
      Integer id,
      Integer requesterUserId) {
    String sql = "SELECT WITHDRAW_REQUEST.*, IOP.invoice_operation_permission_id " +
        " FROM WITHDRAW_REQUEST " +
        " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
        "				(IOP.currency_id=WITHDRAW_REQUEST.currency_id) " +
        "				AND (IOP.user_id=:requester_user_id) " +
        "				AND (IOP.operation_direction=:operation_direction) " +
        " WHERE WITHDRAW_REQUEST.id=:id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("id", id);
      put("requester_user_id", requesterUserId);
      put("operation_direction", "WITHDRAW");
    }};
    return jdbcTemplate.queryForObject(sql, params, (rs, i) -> {
      WithdrawRequestFlatDto withdrawRequestFlatDto = withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
      withdrawRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
      return withdrawRequestFlatDto;
    });
  }

  @Override
  public List<WithdrawRequestPostDto> getForPostByStatusList(Integer statusId) {
    String sql = " SELECT WR.*, " +
        " CUR.name AS currency_name, " +
        " M.name AS merchant_name, M.service_bean_name " +
        " FROM WITHDRAW_REQUEST WR " +
        " JOIN CURRENCY CUR ON (CUR.id = WR.currency_id) " +
        " JOIN MERCHANT M ON (M.id = WR.merchant_id) " +
        " WHERE status_id = :status_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("status_id", statusId);
    }};
    return jdbcTemplate.query(sql, params, (rs, idx) -> {
      WithdrawRequestPostDto result = new WithdrawRequestPostDto();
      result.setId(rs.getInt("id"));
      result.setWallet(rs.getString("wallet"));
      result.setRecipientBankName(rs.getString("recipient_bank_name"));
      result.setRecipientBankCode(rs.getString("recipient_bank_code"));
      result.setUserFullName(rs.getString("user_full_name"));
      result.setRemark(rs.getString("remark"));
      result.setAmount(rs.getBigDecimal("amount"));
      result.setCommissionAmount(rs.getBigDecimal("commission"));
      result.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
      result.setCurrencyName(rs.getString("currency_name"));
      result.setMerchantName(rs.getString("merchant_name"));
      result.setMerchantServiceBeanName(rs.getString("service_bean_name"));
      return result;
    });
  }

  @Override
  public WithdrawRequestFlatAdditionalDataDto getAdditionalDataForId(int id) {
    String sql = "SELECT " +
        "   CUR.name AS currency_name, " +
        "   USER.email AS user_email, " +
        "   ADMIN.email AS admin_email, " +
        "   M.name AS merchant_name, " +
        "   MI.id AS merchant_image_id, " +
        "   MI.image_name AS merchant_image_name " +
        " FROM WITHDRAW_REQUEST WR " +
        " JOIN CURRENCY CUR ON (CUR.id = WR.currency_id) " +
        " JOIN USER USER ON (USER.id = WR.user_id) " +
        " LEFT JOIN USER ADMIN ON (ADMIN.id = WR.admin_holder_id) " +
        " JOIN MERCHANT M ON (M.id = WR.merchant_id) " +
        " LEFT JOIN MERCHANT_IMAGE MI ON MI.id = WR.merchant_image_id" +
        " WHERE WR.id = :id";
    return jdbcTemplate.queryForObject(sql, singletonMap("id", id), (rs, idx) -> {
          WithdrawRequestFlatAdditionalDataDto withdrawRequestFlatAdditionalDataDto = new WithdrawRequestFlatAdditionalDataDto();
          withdrawRequestFlatAdditionalDataDto.setUserEmail(rs.getString("user_email"));
          withdrawRequestFlatAdditionalDataDto.setAdminHolderEmail(rs.getString("admin_email"));
          withdrawRequestFlatAdditionalDataDto.setCurrencyName(rs.getString("currency_name"));
          withdrawRequestFlatAdditionalDataDto.setMerchantName(rs.getString("merchant_name"));
          MerchantImage merchantImage = new MerchantImage();
          merchantImage.setId(rs.getInt("merchant_image_id"));
          merchantImage.setImage_name(rs.getString("merchant_image_name"));
          withdrawRequestFlatAdditionalDataDto.setMerchantImage(merchantImage);
          return withdrawRequestFlatAdditionalDataDto;
        }
    );
  }

  @Override
  public void setHolderById(Integer id, Integer holderId) {
    final String sql = "UPDATE WITHDRAW_REQUEST " +
        "  SET admin_holder_id = :admin_holder_id " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("admin_holder_id", holderId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public void setInPostingStatusByStatus(Integer inPostingStatusId, List<Integer> statusIdList) {
    final String sql =
        "  UPDATE " +
            "    WITHDRAW_REQUEST WR " +
            "    JOIN MERCHANT_CURRENCY MC ON (MC.merchant_id = WR.merchant_id) AND " +
            "     (MC.currency_id = WR.currency_id) AND " +
            "     (MC.withdraw_auto_enabled = 1) AND " +
            "     (WR.status_modification_date <= NOW() - INTERVAL MC.withdraw_auto_delay_seconds SECOND) " +
            "  SET status_id = :new_status_id " +
            "  WHERE WR.status_id IN (:status_id_list)  ";
    Map<String, Object> params = new HashMap<>();
    params.put("status_id_list", statusIdList);
    params.put("new_status_id", inPostingStatusId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<ClientBank> findClientBanksForCurrency(Integer currencyId) {
    final String sql = "SELECT id, currency_id, name, code " +
        " FROM CLIENT_BANK " +
        " WHERE currency_id = :currency_id";
    final Map<String, Integer> params = Collections.singletonMap("currency_id", currencyId);
    return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
      ClientBank bank = new ClientBank();
      bank.setId(rs.getInt("id"));
      bank.setName(rs.getString("name"));
      bank.setCurrencyId(rs.getInt("currency_id"));
      bank.setCode(rs.getString("code"));
      return bank;
    });
  }

}

