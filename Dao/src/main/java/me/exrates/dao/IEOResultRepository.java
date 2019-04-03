package me.exrates.dao;

import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;

import java.math.BigDecimal;

public interface IEOResultRepository {

    IEOResult create(IEOResult ieoResult, BigDecimal availableAmount);

    BigDecimal getAvailableAmount(IEOClaim ieoClaim);
}
