package me.exrates.dao;

import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;

public interface CommissionDao {

	Commission getCommission(OperationType operationType);

}

