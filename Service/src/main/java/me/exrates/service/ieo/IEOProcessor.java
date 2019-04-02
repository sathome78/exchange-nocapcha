package me.exrates.service.ieo;

import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;

public class IEOProcessor implements Runnable {
    private IEOClaimRepository ieoClaimRepository;
    private IEOResultRepository ieoResultRepository;
    private IEOClaim ieoClaim;

    public IEOProcessor(IEOClaimRepository ieoClaimRepository, IEOResultRepository ieoResultRepository, IEOClaim ieoClaim) {
        this.ieoClaimRepository = ieoClaimRepository;
        this.ieoResultRepository = ieoResultRepository;
        this.ieoClaim = ieoClaim;
    }

    @Override
    public void run() {
        //todo check available balances
        //todo implement transaction
        //todo send notification
    }
}
