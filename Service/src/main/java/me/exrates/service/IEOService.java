package me.exrates.service;

import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.dto.ieo.IeoDetailsCreateDto;
import me.exrates.model.dto.ieo.IeoDetailsUpdateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface IEOService {
    ClaimDto addClaim(ClaimDto claimDto, String email);

    IEOStatusInfo checkUserStatusForIEO(String email);

    Collection<IEODetails> findAll();

    IEODetails findOne(int ieoId);

    Collection<IEODetails> findAllExceptForMaker(User user);

    @Transactional
    void createIeo(IeoDetailsCreateDto dto);

    @Transactional
    void updateIeo(Integer id, IeoDetailsUpdateDto dto);
}
