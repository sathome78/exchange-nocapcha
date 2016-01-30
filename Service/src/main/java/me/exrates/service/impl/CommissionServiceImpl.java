package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;

import me.exrates.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommissionServiceImpl implements CommissionService {

	@Autowired  
	CommissionDao commissionDao;
	
	@Override
	public double getCommissionByType(int operationType) {
		return commissionDao.getCommissionByType(operationType);
	}
}