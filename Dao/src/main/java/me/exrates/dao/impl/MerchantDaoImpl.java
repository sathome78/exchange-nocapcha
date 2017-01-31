package me.exrates.dao.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.MerchantImage;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.MerchantImageShortenedDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class MerchantDaoImpl implements MerchantDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    MessageSource messageSource;

    @Override
    public Merchant create(Merchant merchant) {
        final String sql = "INSERT INTO MERCHANT (description, name) VALUES (:description,:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("description",merchant.getDescription())
            .addValue("name",merchant.getName());
        if (jdbcTemplate.update(sql, params, keyHolder)>0) {
            merchant.setId(keyHolder.getKey().intValue());
            return merchant;
        }
        return null;
    }

    @Override
    public Merchant findById(int id) {
        final String sql = "SELECT * FROM MERCHANT WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String,Integer>(){
            {
                put("id", id);
            }
        };
        return jdbcTemplate.queryForObject(sql,params,new BeanPropertyRowMapper<>(Merchant.class));
    }

    @Override
    public List<Merchant> findAll() {
        final String sql = "SELECT * FROM MERCHANT";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Merchant.class));
    }


    @Override
    public List<Merchant> findAllByCurrency(int currencyId) {
        final String sql = "SELECT * FROM MERCHANT WHERE id in (SELECT merchant_id FROM MERCHANT_CURRENCY WHERE currency_id = :currencyId)";
        Map<String, Integer> params = new HashMap<String,Integer>() {
            {
                put("currencyId", currencyId);
            }
        };
        try {
            return jdbcTemplate.query(sql, params, (resultSet, i) -> {
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
        final Map<String, Integer> params = new HashMap<String,Integer>(){
            {
                put("merchant", merchant);
                put("currency", currency);
            }
        };
        return jdbcTemplate.queryForObject(sql,params,BigDecimal.class);
    }

    @Override
    public List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType) {
        String blockClause = "";
        if (operationType == OperationType.INPUT) {
            blockClause = " AND MERCHANT_CURRENCY.refill_block = 0";
        } else if (operationType == OperationType.OUTPUT) {
            blockClause = " AND MERCHANT_CURRENCY.withdraw_block = 0";
        }
        final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description,MERCHANT_CURRENCY.min_sum," +
                " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_commission FROM MERCHANT JOIN MERCHANT_CURRENCY" +
                " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id WHERE MERCHANT_CURRENCY.currency_id in (:currenciesId)" +
                blockClause +
                " order by MERCHANT.merchant_order";

        try {
            return jdbcTemplate.query(sql, Collections.singletonMap("currenciesId",currenciesId), (resultSet, i) -> {
                MerchantCurrency merchantCurrency = new MerchantCurrency();
                merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
                merchantCurrency.setName(resultSet.getString("name"));
                merchantCurrency.setDescription(resultSet.getString("description"));
                merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
                merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
                merchantCurrency.setCommission(resultSet.getBigDecimal("merchant_commission"));
                final String sqlInner = "SELECT * FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
                        " AND currency_id = :currency_id;";
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("merchant_id", resultSet.getInt("merchant_id"));
                params.put("currency_id", resultSet.getInt("currency_id"));
                merchantCurrency.setListMerchantImage(jdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImage.class)));
                return merchantCurrency;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole) {
        String whereClause = currencyId == null ? "" : " AND MERCHANT_CURRENCY.currency_id = :currency_id";

        final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name, MERCHANT_CURRENCY.min_sum," +
                " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_commission, MERCHANT_CURRENCY.withdraw_block," +
                " CURRENCY_LIMIT.min_sum AS min_withdraw_sum FROM MERCHANT " +
                "JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
                "JOIN CURRENCY_LIMIT ON MERCHANT_CURRENCY.currency_id = CURRENCY_LIMIT.currency_id " +
                "WHERE CURRENCY_LIMIT.user_role_id = :user_role_id AND CURRENCY_LIMIT.operation_type_id = 2 " + whereClause;
        Map<String, Integer> paramMap = new HashMap<String, Integer>() {{
            put("currency_id", currencyId);
            put("user_role_id", userRole.getRole());
        }};

        try {
            return jdbcTemplate.query(sql, paramMap, (resultSet, i) -> {
                MerchantCurrencyApiDto merchantCurrencyApiDto = new MerchantCurrencyApiDto();
                merchantCurrencyApiDto.setMerchantId(resultSet.getInt("merchant_id"));
                merchantCurrencyApiDto.setCurrencyId(resultSet.getInt("currency_id"));
                merchantCurrencyApiDto.setName(resultSet.getString("name"));
                merchantCurrencyApiDto.setMinInputSum(resultSet.getBigDecimal("min_sum"));
                merchantCurrencyApiDto.setMinOutputSum(resultSet.getBigDecimal("min_withdraw_sum"));
                merchantCurrencyApiDto.setCommission(resultSet.getBigDecimal("merchant_commission"));
                merchantCurrencyApiDto.setWithdrawBlocked(resultSet.getBoolean("withdraw_block"));
                final String sqlInner = "SELECT id, image_path FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
                        " AND currency_id = :currency_id;";
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("merchant_id", resultSet.getInt("merchant_id"));
                params.put("currency_id", resultSet.getInt("currency_id"));
                merchantCurrencyApiDto.setListMerchantImage(jdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImageShortenedDto.class)));
                return merchantCurrencyApiDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions() {
        final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name AS merchant_name, " +
                " CURRENCY.id AS currency_id, CURRENCY.name AS currency_name, MERCHANT_CURRENCY.merchant_commission," +
                " MERCHANT_CURRENCY.withdraw_block, MERCHANT_CURRENCY.refill_block " +
                " FROM MERCHANT " +
                "JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
                "JOIN CURRENCY ON MERCHANT_CURRENCY.currency_id = CURRENCY.id " +
                "ORDER BY merchant_id, currency_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            MerchantCurrencyOptionsDto dto = new MerchantCurrencyOptionsDto();
            dto.setMerchantId(rs.getInt("merchant_id"));
            dto.setCurrencyId(rs.getInt("currency_id"));
            dto.setMerchantName(rs.getString("merchant_name"));
            dto.setCurrencyName(rs.getString("currency_name"));
            dto.setCommission(rs.getBigDecimal("merchant_commission"));
            dto.setRefillBlocked(rs.getBoolean("refill_block"));
            dto.setWithdrawBlocked(rs.getBoolean("withdraw_block"));
            return dto;
        });
    }



    @Override
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale) {
        String sql = " select TRANSACTION.datetime, CURRENCY.name as currency, TRANSACTION.amount, TRANSACTION.commission_amount, \n" +
                "case when OPERATION_TYPE.name = 'input' or WITHDRAW_REQUEST.merchant_image_id is null then\n" +
                "MERCHANT.name else\n" +
                "MERCHANT_IMAGE.image_name end as merchant,\n" +
                "OPERATION_TYPE.name as operation_type, TRANSACTION.id, TRANSACTION.provided, USER.id AS user_id from TRANSACTION \n" +
                "left join CURRENCY on TRANSACTION.currency_id=CURRENCY.id\n" +
                "left join WITHDRAW_REQUEST on TRANSACTION.id=WITHDRAW_REQUEST.transaction_id\n" +
                "left join MERCHANT_IMAGE on WITHDRAW_REQUEST.merchant_image_id=MERCHANT_IMAGE.id\n" +
                "left join MERCHANT on TRANSACTION.merchant_id = MERCHANT.id \n" +
                "left join OPERATION_TYPE on TRANSACTION.operation_type_id=OPERATION_TYPE.id\n" +
                "left join WALLET on TRANSACTION.user_wallet_id=WALLET.id\n" +
                "left join USER on WALLET.user_id=USER.id\n" +
                "where TRANSACTION.source_type=:source_type and USER.email=:email order by datetime DESC" +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        final Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("source_type", TransactionSourceType.MERCHANT.toString());
        return jdbcTemplate.query(sql, params, new RowMapper<MyInputOutputHistoryDto>() {
            @Override
            public MyInputOutputHistoryDto mapRow(ResultSet rs, int i) throws SQLException {
                MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
                myInputOutputHistoryDto.setDatetime(rs.getTimestamp("datetime").toLocalDateTime());
                myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
                myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
                myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
                myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
                myInputOutputHistoryDto.setOperationType(rs.getString("operation_type"));
                myInputOutputHistoryDto.setTransactionId(rs.getInt("id"));
                myInputOutputHistoryDto.setTransactionProvided(rs.getInt("provided") == 0 ?
                        messageSource.getMessage("inputoutput.statusFalse", null, locale) :
                        messageSource.getMessage("inputoutput.statusTrue", null, locale));
                myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
                return myInputOutputHistoryDto;
            }
        });
    }

    public Integer getInputRequests(int merchantId, String email){
        String sql = "SELECT COUNT(*) FROM birzha.TRANSACTION \n" +
                "join WALLET ON(WALLET.id = TRANSACTION.user_wallet_id)\n" +
                "join USER ON(USER.id = WALLET.user_id)\n" +
                " where \n" +
                " TRANSACTION.source_type = 'MERCHANT' and TRANSACTION.provided = 0 \n" +
                " and USER.email = :email and TRANSACTION.merchant_id = :merchantId \n" +
                " and SUBSTRING_INDEX(TRANSACTION.datetime, ' ', 1) = CURDATE() ; ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("merchantId", merchantId);
        params.put("email", email);
        return jdbcTemplate.queryForObject(sql,params,Integer.class);
    }

    @Override
    public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
        String fieldToToggle = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY SET " + fieldToToggle + " = !" + fieldToToggle +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void setBlockForAll(OperationType operationType, boolean blockStatus) {
        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY SET " + blockField + " = :block";
        Map<String, Integer> params = Collections.singletonMap("block", blockStatus ? 1 : 0);
        jdbcTemplate.update(sql, params);
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
        jdbcTemplate.update(sql, params);
    }

    @Override
    public boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "SELECT " + blockField + " FROM MERCHANT_CURRENCY " +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        return jdbcTemplate.queryForObject(sql, params, Boolean.class);
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


}