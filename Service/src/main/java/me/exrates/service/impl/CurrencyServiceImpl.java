package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private CommissionService commissionService;

    private static final Logger logger = LogManager.getLogger(CurrencyServiceImpl.class);
    private static final Set<String> CRYPTO = new HashSet<String>() {
        {
            add("EDRC");
            add("BTC");
            add("LTC");
        }
    };
    private static final int CRYPTO_PRECISION = 8;
    private static final int DEFAULT_PRECISION = 2;

    @Override
    public String getCurrencyName(int currencyId) {
        return currencyDao.getCurrencyName(currencyId);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyDao.getCurrList();
    }

    @Override
    public Currency findByName(String name) {
        return currencyDao.findByName(name);
    }

    @Override
    public Currency findById(int id) {
        return currencyDao.findById(id);
    }

    @Override
    public List<Currency> findAllCurrencies() {
        return currencyDao.findAllCurrencies();
    }

    @Override
    public boolean updateMinWithdraw(int currencyId, BigDecimal minAmount) {
        return currencyDao.updateMinWithdraw(currencyId, minAmount);
    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount) {
        List<Integer> roleIds = commissionService.resolveRoleIdsByName(roleName);
        currencyDao.updateCurrencyLimit(currencyId, operationType, roleIds, minAmount);
    }

    @Override
    public List<CurrencyLimit> retrieveCurrencyLimitsForRole(String roleName, OperationType operationType) {
        List<Integer> roleIds = commissionService.resolveRoleIdsByName(roleName);
        return currencyDao.retrieveCurrencyLimitsForRoles(roleIds, operationType);
    }

    @Override
    public BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        return currencyDao.retrieveMinLimitForRoleAndCurrency(userRole, operationType, currencyId);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs() {
        return currencyDao.getAllCurrencyPairs();
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        try {
            return currencyDao.findCurrencyPairById(currencyPairId);
        } catch (EmptyResultDataAccessException e) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    @Override
    public String amountToString(final BigDecimal amount, final String currency) {
        return amount.setScale(resolvePrecision(currency), ROUND_HALF_UP)
//                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    public int resolvePrecision(final String currency) {
        return CRYPTO.contains(currency) ? CRYPTO_PRECISION : DEFAULT_PRECISION;
    }
}
