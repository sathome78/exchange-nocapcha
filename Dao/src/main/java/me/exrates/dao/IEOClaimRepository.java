package me.exrates.dao;

import me.exrates.model.IEOClaim;

import java.util.Collection;

public interface IEOClaimRepository {

    IEOClaim create(IEOClaim ieoClaim);

    Collection<IEOClaim> findUnprocessedIeoClaims();

    boolean updateStateIEOClaim(int id, IEOClaim.IEOClaimStateEnum state);

    boolean checkIfIeoOpenForCurrency(String currencyName);
}
