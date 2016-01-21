package me.exrates.service;

import me.exrates.dao.CommissionDao;

import org.springframework.beans.factory.annotation.Autowired;

public class CommissionServiceImpl implements CommissionService {

	@Autowired  
	CommissionDao commissionDao;
	
	@Override
	public double getCommissionByType(int operationType) {
		return commissionDao.getCommissionByType(operationType);
	}

}
