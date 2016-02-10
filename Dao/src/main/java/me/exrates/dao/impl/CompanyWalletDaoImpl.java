package me.exrates.dao.impl;

import me.exrates.dao.CompanyWalletDao;
import me.exrates.model.CompanyWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class CompanyWalletDaoImpl implements CompanyWalletDao {

    @Autowired
    private DataSource dataSource;


    @Override
    public CompanyWallet create(CompanyWallet companyWallet) {
        final String sql = "INSERT INTO COMPANY_WALLET(currency_id) VALUES (:currencyId)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("currencyId",companyWallet.getCurrencyId());
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        if (jdbcTemplate.update(sql,params,keyHolder)>0) {
            return findByCurrencyId(companyWallet.getCurrencyId());
        }
        return null;
    }

    @Override
    public CompanyWallet findByCurrencyId(int currencyId) {
        final String sql = "SELECT COMPANY_WALLET.id,COMPANY_WALLET.currency_id,CURRENCY.name as currency_name FROM " +
                "COMPANY_WALLET JOIN CURRENCY ON COMPANY_WALLET.currency_id = CURRENCY.id WHERE currency_id = :currencyId";
        final Map<String,Integer> params = new HashMap<>();
        params.put("currencyId",currencyId);
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        CompanyWallet companyWallet = new CompanyWallet();
        try {
            jdbcTemplate.query(sql, params, (resultSet, i) -> {
                companyWallet.setId(resultSet.getInt("id"));
                companyWallet.setCurrencyId(resultSet.getInt("currency_id"));
                companyWallet.setName(resultSet.getString("currency_name"));
                return companyWallet;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return companyWallet;
    }
}