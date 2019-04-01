package me.exrates.service;

import me.exrates.model.dto.ieo.ClaimDto;

public interface IEOService {
    ClaimDto addClaim(ClaimDto claimDto, String email);

}
