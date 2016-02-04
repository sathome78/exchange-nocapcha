package me.exrates.dao;

import me.exrates.model.enums.OperationType;

public interface CommissionDao {

	double getCommissionByType(OperationType operationType);
}

