package me.exrates.dao.impl;

import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.QuberaDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.QuberaUserData;
import me.exrates.model.dto.qubera.QuberaRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

@Repository
@Log4j2
public class QuberaDaoImpl implements QuberaDao {

    private static final String columns = "user_id, currency_id, account_number, iban, address, city, first_name, last_name, country_code";

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    private final RowMapper<QuberaUserData> quberaUserDataRowMapper = (rs, row) -> {
        QuberaUserData quberaUserData = new QuberaUserData();
        quberaUserData.setUserId(rs.getInt("user_id"));
        quberaUserData.setAccountNumber(rs.getString("account_number"));
        quberaUserData.setIban(rs.getString("iban"));
        quberaUserData.setCurrencyId(rs.getInt("currency_id"));
        quberaUserData.setFirsName(rs.getObject("first_name") == null ? null : rs.getString("first_name"));
        quberaUserData.setLastName(rs.getObject("last_name") == null ? null : rs.getString("last_name"));
        quberaUserData.setAddress(rs.getObject("address") == null ? null : rs.getString("address"));
        quberaUserData.setCity(rs.getObject("city") == null ? null : rs.getString("city"));
        quberaUserData.setCountryCode(rs.getObject("country_code") == null ? null : rs.getString("country_code"));
        return quberaUserData;
    };

    @Autowired
    public QuberaDaoImpl(@Qualifier("masterTemplate") NamedParameterJdbcTemplate masterJdbcTemplate,
                         @Qualifier("slaveTemplate") NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public boolean saveUserDetails(QuberaUserData userData) {
        String sql = "INSERT INTO QUBERA_USER_DETAILS (user_id, currency_id, account_number, iban, address, city, first_name, last_name, country_code) "
                + " VALUES (user_id = :user_id, currency_id = :currency_id, account_number = :account_number, iban = :iban, address = :address, city = :city, " +
                "first_name = :first_name, last_name = :last_name, country_code = :country_code ) "
                + " ON DUPLICATE KEY UPDATE account_number = :account_number, iban = :iban";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userData.getUserId());
        params.addValue("currency_id", userData.getCurrencyId());
        params.addValue("account_number", userData.getAccountNumber());
        params.addValue("iban", userData.getIban());
        params.addValue("address", userData.getAddress());
        params.addValue("city", userData.getCity());
        params.addValue("first_name", userData.getFirsName());
        params.addValue("last_name", userData.getLastName());
        params.addValue("country_code", userData.getCountryCode());
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public Map<String, String> getUserDetailsForCurrency(int userId, int currencyId) {
        String sql = "SELECT account_number, iban FROM QUBERA_USER_DETAILS "
                + " WHERE user_id = :user_id AND currency_id = :currency_id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("currency_id", currencyId);
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                Map<String, String> values = Maps.newHashMap();
                values.put("accountNumber", rs.getString("account_number"));
                values.put("iban", rs.getString("iban"));
                return values;
            });
        } catch (EmptyResultDataAccessException e) {
            log.info("Neither iban nor accountNumber for userId: {} and currencyId: {}", userId, currencyId);
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean existAccountByUserEmailAndCurrencyName(String email, String currency) {
        String sql = "SELECT CASE WHEN count(*) > 0" +
                " THEN TRUE ELSE FALSE END" +
                " FROM QUBERA_USER_DETAILS qud" +
                " INNER JOIN USER u on qud.user_id = u.id" +
                " INNER JOIN CURRENCY c on qud.currency_id = c.id" +
                " WHERE u.email = :email AND c.name = :currency";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("currency", currency);

        return slaveJdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    @Override
    public String getAccountByUserEmail(String email) {
        String sql = "SELECT account_number" +
                " FROM QUBERA_USER_DETAILS" +
                " INNER JOIN USER u on QUBERA_USER_DETAILS.user_id = u.id" +
                " WHERE u.email = :email";

        return slaveJdbcTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
    }

    @Override
    public QuberaUserData getUserDataByUserId(int userId) {
        String sql = "SELECT " + columns + " FROM QUBERA_USER_DETAILS WHERE user_id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        return slaveJdbcTemplate.queryForObject(sql, params, quberaUserDataRowMapper);
    }

    @Override
    public Integer findUserIdByAccountNumber(String accountNumber) {
        String sql = "SELECT qud.user_id FROM QUBERA_USER_DETAILS qud WHERE qud.account_number = :account_number;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_number", accountNumber);
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("User not found for accountNumber: %s", accountNumber);
            log.info(message);
            throw new UserNotFoundException(message);
        }
    }

    @Override
    public boolean logResponse(QuberaRequestDto requestDto) {
        String sql = "INSERT INTO QUBERA_RESPONSE_LOG (paymentId , messageId, accountIBAN, accountNumber, processingTime,"
                + " state, currency, paymentAmount, transferType, rejectionReason) "
                + " VALUES (paymentId =:paymentId, messageId = :messageId, accountIBAN = :accountIBAN, accountNumber = :accountNumber,"
                + " processingTime = :processingTime, state = :state, currency = :currency, paymentAmount = :paymentAmount,"
                + " transferType = :transferType, rejectionReason = :rejectionReason)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("paymentId", requestDto.getPaymentId());
        params.addValue("messageId", requestDto.getMessageId());
        params.addValue("accountIBAN", requestDto.getAccountIBAN());
        params.addValue("accountNumber", requestDto.getAccountNumber());
        params.addValue("processingTime", Timestamp.valueOf(requestDto.getProcessingTime()));
        params.addValue("state", requestDto.getState());
        params.addValue("currency", requestDto.getCurrency());
        params.addValue("paymentAmount", requestDto.getPaymentAmount());
        params.addValue("transferType", requestDto.getTransferType());
        params.addValue("rejectionReason", requestDto.getRejectionReason());
        return masterJdbcTemplate.update(sql, params) > 0;
    }
}