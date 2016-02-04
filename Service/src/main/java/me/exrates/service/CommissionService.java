package me.exrates.service;

import me.exrates.model.enums.OperationType;

public interface CommissionService {

	double getCommissionByType(OperationType operationType);
}
