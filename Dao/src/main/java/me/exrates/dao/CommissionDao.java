package me.exrates.dao;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

public interface CommissionDao {

	Commission getCommission(OperationType operationType);

	BigDecimal getCommissionMerchant(String merchant, String currency);

	}

