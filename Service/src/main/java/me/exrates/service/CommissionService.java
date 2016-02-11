package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;

public interface CommissionService {
	Commission findCommissionByType(OperationType operationType);
	double getCommissionByType(OperationType operationType);
}
