package me.exrates.service;

import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.dto.ieo.IeoUserStatus;

public interface IEOService {
    ClaimDto addClaim(ClaimDto claimDto, String email);

    IEOStatusInfo checkUserStatusForIEO(String email);

}
