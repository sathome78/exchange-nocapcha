package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CommissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommissionServiceImpl implements CommissionService {

	@Autowired  
	CommissionDao commissionDao;

	@Override
	public Commission findCommissionByType(OperationType operationType) {
		return commissionDao.getCommission(operationType);
	}

	@Override
	public double getCommissionByType(OperationType type) {
		return commissionDao.getCommissionByType(type);
	}

}
