package me.exrates.service;

import me.exrates.dao.CommissionDao;
import me.exrates.model.enums.OperationType;

import org.springframework.beans.factory.annotation.Autowired;

public class CommissionServiceImpl implements CommissionService {

	@Autowired  
	CommissionDao commissionDao;
	
	@Override
	public double getCommissionByType(OperationType type) {
		return commissionDao.getCommissionByType(type);
	}

}
