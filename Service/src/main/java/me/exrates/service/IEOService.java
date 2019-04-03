package me.exrates.service;

import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.dto.ieo.IeoUserStatus;

import java.util.Collection;

public interface IEOService {
    ClaimDto addClaim(ClaimDto claimDto, String email);

    IEOStatusInfo checkUserStatusForIEO(String email);

    Collection<IEODetails> findAll();

    Collection<IEODetails> findAllExceptForMaker(User user);

}
