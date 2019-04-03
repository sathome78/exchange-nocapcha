package me.exrates.service.ieo;

import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;
import me.exrates.service.WalletService;

import java.math.BigDecimal;

public class IEOProcessor implements Runnable {

    private final IEOResultRepository ieoResultRepository;
    private final IEOClaimRepository ieoClaimRepository;
    private final WalletService walletService;
    private final IEOClaim ieoClaim;

    public IEOProcessor(IEOResultRepository ieoResultRepository,
                        IEOClaimRepository ieoClaimRepository, IEOClaim ieoClaim,
                        WalletService walletService) {
        this.ieoResultRepository = ieoResultRepository;
        this.ieoClaimRepository = ieoClaimRepository;
        this.walletService = walletService;
        this.ieoClaim = ieoClaim;
    }

    @Override
    public void run() {
        BigDecimal availableAmount = ieoResultRepository.getAvailableAmount(ieoClaim);
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            // todo update notification message
            return;
        }
        if (availableAmount.compareTo(ieoClaim.getAmount()) < 0) {
            // todo update notification message
            String notoficationMessage = String.format("Unfortunately, the available amount of is %s ", availableAmount.toPlainString());

            BigDecimal oldAmount = ieoClaim.getAmount();
            BigDecimal remainingAmount = oldAmount.subtract(availableAmount);
            BigDecimal btcRollBack = remainingAmount.multiply(ieoClaim.getRate());
            walletService.rollbackUserBtcForIeo(ieoClaim.getUserId(), btcRollBack);
            ieoClaim.setAmount(availableAmount);
            BigDecimal newPriceInBtc = remainingAmount.multiply(ieoClaim.getRate());
            ieoClaim.setPriceInBtc(newPriceInBtc);
            availableAmount = BigDecimal.ZERO;
        } else {
            availableAmount = availableAmount.subtract(ieoClaim.getAmount());
        }

        IEOResult.IEOResultStatus resultStatus = walletService.performIeoTransfer(ieoClaim)
                ? IEOResult.IEOResultStatus.success
                : IEOResult.IEOResultStatus.fail;

        IEOResult ieoResult = IEOResult.builder()
                .claimId(ieoClaim.getId())
                .availableAmount(availableAmount)
                .status(resultStatus)
                .build();

        ieoResultRepository.create(ieoResult);
        ieoClaimRepository.updateClaimStatus(ieoClaim.getId());

        // todo send notification

        // todo send message to websocket
    }
}
