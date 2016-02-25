package me.exrates.dao.impl;

import me.exrates.dao.CompanyWalletDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import org.omg.SendingContext.RunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.AbstractMap.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class CompanyWalletDaoImpl implements CompanyWalletDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public CompanyWallet create(Currency currency) {
        final String sql = "INSERT INTO COMPANY_WALLET(currency_id) VALUES (:currencyId)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final Map<String,Integer> params = unmodifiableMap(of(
                new SimpleEntry<>("currencyId",currency.getId()
        )).collect(toMap(SimpleEntry::getKey,SimpleEntry::getValue)));
        if (jdbcTemplate.update(sql,new MapSqlParameterSource(params),keyHolder)>0) {
            final CompanyWallet companyWallet = new CompanyWallet();
            companyWallet.setCurrency(currency);
            companyWallet.setId(keyHolder.getKey().intValue());
            return companyWallet;
        }
        return null;
    }

    @Override
    public CompanyWallet findByCurrencyId(Currency currency) {
        final String sql = "SELECT COMPANY_WALLET.id FROM COMPANY_WALLET JOIN CURRENCY ON COMPANY_WALLET.currency_id = CURRENCY.id WHERE currency_id = :currencyId";
        final Map<String,Integer> params = unmodifiableMap(of(
                new SimpleEntry<>("currencyId",currency.getId()
                )).collect(toMap(SimpleEntry::getKey,SimpleEntry::getValue)));
        try {
            jdbcTemplate.query(sql, params, (resultSet, i) -> {
                final CompanyWallet companyWallet = new CompanyWallet();
                companyWallet.setId(resultSet.getInt("id"));
                companyWallet.setCurrency(currency);
                return companyWallet;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return null;
    }

    @Override
    public boolean update(CompanyWallet companyWallet) {
        final String sql = "UPDATE COMPANY_WALLET SET balance := balance, commission_balance = :commissionBalance";
        final Map<String,BigDecimal> params = unmodifiableMap(of(
                new SimpleEntry<>("balance",companyWallet.getBalance()),
                new SimpleEntry<>("commissionBalance",companyWallet.getCommissionBalance())
                ).collect(toMap(SimpleEntry::getKey,SimpleEntry::getValue)));
        return jdbcTemplate.update(sql, params) > 0;
    }
}