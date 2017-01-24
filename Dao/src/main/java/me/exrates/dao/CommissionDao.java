package me.exrates.dao;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;

public interface CommissionDao {

	Commission getCommission(OperationType operationType, UserRole userRole);

    Commission getDefaultCommission(OperationType operationType);

    BigDecimal getCommissionMerchant(String merchant, String currency);

    List<Commission> getEditableCommissions();

    void updateCommission(Integer id, BigDecimal value);

    void updateMerchantCurrencyCommission(Integer merchantId, Integer currencyId, BigDecimal value);
}

