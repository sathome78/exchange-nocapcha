package me.exrates.dao;

import me.exrates.model.IEOClaim;

public interface IEOClaimRepository {

    IEOClaim create(IEOClaim ieoClaim);

    boolean updateStateIEOClaim(int id, IEOClaim.IEOClaimStateEnum state);

}
