package me.exrates.dao;

import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;

import java.math.BigDecimal;

public interface IEOResultRepository {

    IEOResult save(IEOResult ieoResult);

    IEOResult startIeo(IEOClaim ieoClaim);

    boolean isAlreadyStarted(IEOClaim ieoClaim);

    BigDecimal getAvailableAmount(IEOClaim ieoClaim);
}
