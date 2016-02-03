package me.exrates.service;

import me.exrates.model.enums.OperationType;

public interface CommissionService {

	public double getCommissionByType(OperationType type);
}
