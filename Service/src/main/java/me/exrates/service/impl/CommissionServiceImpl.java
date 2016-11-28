package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CommissionServiceImpl implements CommissionService {


	@Autowired  
	CommissionDao commissionDao;

	@Override
	public Commission findCommissionByType(OperationType operationType) {
		return commissionDao.getCommission(operationType);
	}

	@Override
	public BigDecimal getCommissionMerchant(String merchant, String currency) {
		return commissionDao.getCommissionMerchant(merchant, currency);
	}

	@Override
	public List<Commission> getEditableCommissions() {
		return commissionDao.getEditableCommissions();
	}

	@Override
	@Transactional
	public void updateCommission(Integer id, BigDecimal value) {
		commissionDao.updateCommission(id, value);
	}

	@Override
	@Transactional
	public void updateMerchantCommission(Integer merchantId, Integer currencyId, BigDecimal value) {
		commissionDao.updateMerchantCurrencyCommission(merchantId, currencyId, value);
	}

}
