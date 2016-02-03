package me.exrates.dao;

import me.exrates.model.enums.OperationType;

public interface CommissionDao {

	public double getCommissionByType(OperationType type);
}
