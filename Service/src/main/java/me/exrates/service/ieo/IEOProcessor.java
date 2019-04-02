package me.exrates.service.ieo;

import me.exrates.dao.IEOInfoRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;
import me.exrates.service.WalletService;

import java.math.BigDecimal;

public class IEOProcessor implements Runnable {

    private final IEOResultRepository ieoResultRepository;
    private final WalletService walletService;
    private final IEOClaim ieoClaim;

    public IEOProcessor(IEOResultRepository ieoResultRepository,
                        IEOClaim ieoClaim,
                        WalletService walletService) {
        this.ieoResultRepository = ieoResultRepository;
        this.walletService = walletService;
        this.ieoClaim = ieoClaim;
    }

    @Override
    public void run() {
        BigDecimal availableAmount = ieoResultRepository.getAvailableBalance(ieoClaim);
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

        // save result
        IEOResult ieoResult = IEOResult.builder().claimId();

        // subtract btc from user ieo_reserve to maker btc and add currency

        // send btc to maker

        // update claims

        // send notification

        // send message to websocket

    }
}
