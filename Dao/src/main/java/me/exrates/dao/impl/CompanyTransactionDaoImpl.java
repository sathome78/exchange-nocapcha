package me.exrates.dao.impl;

import me.exrates.dao.CompanyTransactionDao;
import me.exrates.model.CompanyTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class CompanyTransactionDaoImpl implements CompanyTransactionDao {

    @Autowired
    private DataSource dataSource;

    @Override
    public CompanyTransaction create(CompanyTransaction companyTransaction) {
        final String sql = "INSERT INTO COMPANY_TRANSACTION(wallet_id, sum, currency_id, operation_type_id, merchant_id,date) " +
                "VALUES (:walletId,:sum,:currencyId,:operationTypeId,:merchantId,:date)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        final LocalDateTime date = LocalDateTime.now();
        params.addValue("walletId",companyTransaction.getWalletId());
        params.addValue("sum",companyTransaction.getSum());
        params.addValue("currencyId",companyTransaction.getCurrencyId());
        params.addValue("operationTypeId",companyTransaction.getOperationTypeId());
        params.addValue("merchantId",companyTransaction.getMerchantId());
        params.addValue("date", Timestamp.valueOf(date));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        if (jdbcTemplate.update(sql, params, keyHolder)>0) {
            companyTransaction.setId(keyHolder.getKey().intValue());
            companyTransaction.setDate(date);
            return companyTransaction;
        }
        return null;
    }
}