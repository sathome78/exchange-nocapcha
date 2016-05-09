package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

public interface CommissionService {
	
	Commission findCommissionByType(OperationType operationType);

	BigDecimal getCommissionMerchant(String merchant, String currency);
	
}
