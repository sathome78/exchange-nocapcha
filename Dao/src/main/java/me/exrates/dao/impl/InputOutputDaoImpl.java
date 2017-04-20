package me.exrates.dao.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ValkSam on 16.04.2017.
 */
@Repository
public class InputOutputDaoImpl implements InputOutputDao {

  private static final Logger log = LogManager.getLogger("inputoutput");

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Autowired
  private MessageSource messageSource;

  @Override
  public List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
      String email,
      Integer offset,
      Integer limit,
      List<Integer> operationTypeIdList,
      Locale locale) {
    String sql = " SELECT " +
        "    IF (WITHDRAW_REQUEST.date_creation IS NOT NULL, WITHDRAW_REQUEST.date_creation, REFILL_REQUEST.date_creation) AS datetime, " +
        "    CURRENCY.name as currency, TRANSACTION.amount, TRANSACTION.commission_amount, " +
        "    MERCHANT.name AS merchant,  " +
        "    TRANSACTION.source_type AS source_type, " +
        "    OPERATION_TYPE.name as operation_type, TRANSACTION.id AS transaction_id, " +
        "    IF (WITHDRAW_REQUEST.id IS NOT NULL, WITHDRAW_REQUEST.id, REFILL_REQUEST.id) AS operation_id," +
        "    (SELECT MAX(confirmation_number) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = RR.id) AS confirmation, " +
        "    IF(WITHDRAW_REQUEST.wallet IS NOT NULL, WITHDRAW_REQUEST.wallet, INVOICE_BANK.account_number) AS destination, " +
        "    USER.id AS user_id," +
        "    IF (WITHDRAW_REQUEST.status_id IS NOT NULL, WITHDRAW_REQUEST.status_id, REFILL_REQUEST.status_id) AS status_id," +
        "    IF (WITHDRAW_REQUEST.status_modification_date IS NOT NULL, WITHDRAW_REQUEST.status_modification_date, REFILL_REQUEST.status_modification_date) AS status_modification_date," +
        "    IF (WITHDRAW_REQUEST.user_full_name IS NOT NULL, WITHDRAW_REQUEST.user_full_name, REFILL_REQUEST.user_full_name) AS user_full_name," +
        "    IF (WITHDRAW_REQUEST.remark IS NOT NULL, WITHDRAW_REQUEST.remark, REFILL_REQUEST.remark) AS remark," +
        "    IF (WITHDRAW_REQUEST.admin_holder_id IS NOT NULL, WITHDRAW_REQUEST.admin_holder_id, REFILL_REQUEST.admin_holder_id) AS admin_holder_id" +
        "  FROM TRANSACTION " +
        "    left join CURRENCY on TRANSACTION.currency_id=CURRENCY.id" +
        "    left join WITHDRAW_REQUEST on TRANSACTION.source_type = 'WITHDRAW' AND WITHDRAW_REQUEST.id = TRANSACTION.source_id" +
        "    left join REFILL_REQUEST on TRANSACTION.source_type = 'REFILL' AND REFILL_REQUEST.id = TRANSACTION.source_id" +
        "    left join INVOICE_BANK on INVOICE_BANK.id = REFILL_REQUEST.recipient_bank_id " +
        "    left join MERCHANT on (MERCHANT.id = REFILL_REQUEST.merchant_id) OR (MERCHANT.id = WITHDRAW_REQUEST.merchant_id)" +
        "    left join MERCHANT_IMAGE on MERCHANT_IMAGE.id = WITHDRAW_REQUEST.merchant_image_id " +
        "    left join OPERATION_TYPE on OPERATION_TYPE.id = TRANSACTION.operation_type_id" +
        "    left join WALLET on WALLET.id = TRANSACTION.user_wallet_id" +
        "    left join USER on WALLET.user_id=USER.id" +
        "  WHERE " +
        "    TRANSACTION.operation_type_id IN (:operation_type_id_list) and " +
        "    USER.email=:email " +

        "  UNION " +
        "  (SELECT " +
        "     RR.date_creation, " +
        "     CUR.name, RR.amount, RR.commission, " +
        "     M.name, " +
        "     'REFILL', " +
        "     'Input', NULL, " +
        "     RR.id, " +
        "     (SELECT MAX(confirmation_number) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = RR.id), " +
        "     INVOICE_BANK.account_number, " +
        "     USER.id, " +
        "     RR.status_id, " +
        "     RR.status_modification_date, " +
        "     RR.user_full_name, " +
        "     RR.remark, " +
        "     RR.admin_holder_id" +
        "   FROM REFILL_REQUEST RR " +
        "     JOIN CURRENCY CUR ON CUR.id=RR.currency_id " +
        "     JOIN USER USER ON USER.id=RR.user_id " +
        "     JOIN MERCHANT M ON M.id=RR.merchant_id " +
        "     LEFT JOIN INVOICE_BANK on INVOICE_BANK.id = RR.recipient_bank_id " +
        "   WHERE USER.email=:email AND " +
        "     NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type='REFILL' AND TX.source_id=RR.id AND TX.operation_type_id=1) " +
        "  )  " +

        "  UNION " +
        "  (SELECT " +
        "     WR.date_creation, " +
        "     CUR.name, WR.amount, WR.commission, " +
        "     M.name, " +
        "     'WITHDRAW', " +
        "     'Output', NULL, " +
        "     WR.id, " +
        "     NULL, " +
        "     WR.wallet, " +
        "     null, " +
        "     USER.id, " +
        "     WR.status_id, " +
        "     WR.status_modification_date, " +
        "     WR.user_full_name, " +
        "     WR.remark, " +
        "     WR.admin_holder_id" +
        "   FROM WITHDRAW_REQUEST WR " +
        "     JOIN CURRENCY CUR ON CUR.id=WR.currency_id " +
        "     JOIN USER USER ON USER.id=WR.user_id " +
        "     JOIN MERCHANT M ON M.id=WR.merchant_id " +
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
      myInputOutputHistoryDto.setConfirmation(rs.getInt("confirmation"));
      myInputOutputHistoryDto.setTransactionId(rs.getInt("transaction_id"));
      myInputOutputHistoryDto.setProvided(myInputOutputHistoryDto.getTransactionId() == null ? 0 : 1);
      myInputOutputHistoryDto.setTransactionProvided(myInputOutputHistoryDto.getProvided() == 0 ?
          messageSource.getMessage("inputoutput.statusFalse", null, locale) :
          messageSource.getMessage("inputoutput.statusTrue", null, locale));
      myInputOutputHistoryDto.setId(rs.getInt("id"));
      myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
      myInputOutputHistoryDto.setBankAccount(rs.getString("destination"));
      TransactionSourceType sourceType = TransactionSourceType.convert(rs.getString("source_type"));
      myInputOutputHistoryDto.setSourceType(sourceType);
      myInputOutputHistoryDto.setStatus(rs.getInt("status_id"));
      myInputOutputHistoryDto.setStatusUpdateDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
      myInputOutputHistoryDto.setUserFullName(rs.getString("user_full_name"));
      myInputOutputHistoryDto.setRemark(rs.getString("remark"));
      myInputOutputHistoryDto.setAdminHolderId(rs.getInt("admin_holder_id"));
      return myInputOutputHistoryDto;
    });
  }
}
