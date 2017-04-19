package me.exrates.dao.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Stream;

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
        "   WITHDRAW_REQUEST.wallet AS withdraw_recipient_account" +
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
        "   FROM REFILL_REQUEST RR " +
        "     JOIN CURRENCY CUR ON CUR.id=RR.currency_id " +
        "     JOIN USER USER ON USER.id=RR.user_id " +
        "     JOIN MERCHANT M ON M.id=RR.merchant_id " +
        "     LEFT JOIN MERCHANT_IMAGE MI ON MI.id=RR.merchant_image_id " +
        "   WHERE USER.email=:email AND " +
        "     NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type='WITHDRAW' AND TX.source_id=WR.id AND TX.operation_type_id=2) " +
        "  )  " +


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
}
