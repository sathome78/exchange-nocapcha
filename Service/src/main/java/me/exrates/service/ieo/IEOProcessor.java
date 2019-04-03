package me.exrates.service.ieo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEODetails;
import me.exrates.model.IEOResult;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.WsMessageObject;
import me.exrates.model.enums.WsMessageTypeEnum;
import me.exrates.service.WalletService;
import me.exrates.service.exception.IeoException;
import me.exrates.service.stomp.StompMessenger;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class IEOProcessor implements Runnable {

    private final IEOResultRepository ieoResultRepository;
    private final IEOClaimRepository ieoClaimRepository;
    private final IeoDetailsRepository ieoDetailsRepository;
    private final WalletService walletService;
    private final IEOClaim ieoClaim;
    private final ObjectMapper objectMapper;
    private final StompMessenger stompMessenger;

    public IEOProcessor(IEOResultRepository ieoResultRepository,
                        IEOClaimRepository ieoClaimRepository,
                        IeoDetailsRepository ieoDetailsRepository,
                        IEOClaim ieoClaim,
                        WalletService walletService,
                        ObjectMapper objectMapper,
                        StompMessenger stompMessenger) {
        this.ieoResultRepository = ieoResultRepository;
        this.ieoClaimRepository = ieoClaimRepository;
        this.ieoDetailsRepository = ieoDetailsRepository;
        this.walletService = walletService;
        this.ieoClaim = ieoClaim;
        this.objectMapper = objectMapper;
        this.stompMessenger = stompMessenger;
    }

    @Override
    public void run() {
        IEODetails ieoDetails = ieoDetailsRepository.findOne(ieoClaim.getIeoId());
        if (ieoDetails == null) {
            String message = String.format("Failed to find ieo details for id: %d", ieoClaim.getIeoId());
            log.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_DETAILS_NOT_FOUND, message);
        }
        BigDecimal availableAmount = ieoDetails.getAvailableAmount();
        boolean firstTransaction = false;
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            if (ieoResultRepository.isAlreadyStarted(ieoClaim)) {
                // todo update notification message
                return;
            }
            firstTransaction = true;
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

        IEOResult.IEOResultStatus status = IEOResult.IEOResultStatus.SUCCESS;
        IEOResult ieoResult = IEOResult.builder()
                .claimId(ieoClaim.getId())
                .availableAmount(availableAmount)
                .status(status)
                .build();
        if (firstTransaction) {
            ieoResult.setAvailableAmount(ieoDetails.getAmount());
            ieoResult.setClaimId(-1);
        } else {
            if (!walletService.performIeoTransfer(ieoClaim)) {
                status = IEOResult.IEOResultStatus.FAILED;
            }
            ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), status);
        }
        ieoResultRepository.save(ieoResult);
        ieoDetailsRepository.updateAvailableAmount(ieoClaim.getIeoId(), availableAmount);
        ieoDetails.setAvailableAmount(availableAmount);
        String userEmail = ""; /*todo get email of user which we want to send message*/
        CompletableFuture.runAsync(() -> sendNotifications(userEmail, ieoDetails, new Object()/*object for notification*/));
    }

    private void sendNotifications(String userEmail, IEODetails ieoDetails, Object notificationObject) {
        try {
            String payload = objectMapper.writeValueAsString(new WsMessageObject(WsMessageTypeEnum.IEO, notificationObject));
            stompMessenger.sendPersonalMessageToUser(userEmail, payload);
        } catch (Exception e) {
            /*ignore*/
        }
        try {
            stompMessenger.sendPersonalDetailsIeo(userEmail, objectMapper.writeValueAsString(ImmutableList.of(ieoDetails)));
        } catch (Exception e) {
            /*ignore*/
        }
        try {
            stompMessenger.sendDetailsIeo(ieoDetails.getId(), objectMapper.writeValueAsString(ieoDetails));
        } catch (Exception e) {
            /*ignore*/
        }
    }
}
