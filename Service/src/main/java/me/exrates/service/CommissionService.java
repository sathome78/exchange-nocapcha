package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface CommissionService {
	
	Commission findCommissionByType(OperationType operationType);

	/**
	 * Returns individual commission for current merchant
	 * @param merchant
	 * @param currency
	 * @return BigDecimal commission
	 */
	BigDecimal getCommissionMerchant(String merchant, String currency);

    List<Commission> getEditableCommissions();

    void updateCommission(Integer id, BigDecimal value);

    @Transactional
    void updateMerchantCommission(Integer merchantId, Integer currencyId, BigDecimal value);
}
