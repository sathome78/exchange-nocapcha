package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

public interface CommissionService {
	
	Commission findCommissionByType(OperationType operationType);

	/**
	 * Returns individual commission for current merchant
	 * @param merchant
	 * @param currency
	 * @return BigDecimal commission
	 */
	BigDecimal getCommissionMerchant(String merchant, String currency);
	
}
