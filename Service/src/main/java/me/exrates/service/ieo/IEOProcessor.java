package me.exrates.service.ieo;

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
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.IeoException;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
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
        log.error(">>>>>>>>>>> IEO: ***** START PROCESSING CLAIM # {} *******************", ieoClaim.getIeoId());
        IEODetails ieoDetails = ieoDetailsRepository.findOne(ieoClaim.getIeoId());
        log.error(">>>>>>>>>>> IEO: IEODetails: {}", ieoDetails);
        if (ieoDetails == null) {
            String message = String.format("Failed to find ieo details for id: %d", ieoClaim.getIeoId());
            log.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_DETAILS_NOT_FOUND, message);
        }
        BigDecimal availableAmount = ieoDetails.getAvailableAmount();
        boolean firstTransaction = false;
        log.error(">>>>>>>>>>> IEO: firstTransaction: {}", firstTransaction);
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            if (ieoResultRepository.isAlreadyStarted(ieoClaim)) {
                log.error(">>>>>>>>>>> IEO: The IEO is running, but available balance is 0");
                // todo update notification message
                return;
            }
            log.error("The IEO is running, but it's first transaction");
            firstTransaction = true;
        }
        if (availableAmount.compareTo(ieoClaim.getAmount()) < 0) {
            log.error(">>>>>>>>>>> IEO: The claim amount is greater than available ({} vs {}))", ieoClaim.getAmount(), availableAmount);
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
            log.error(">>>>>>>>>>> IEO: The available  amount is {} now after claim {}", availableAmount, ieoClaim.getAmount());
        }

        IEOResult.IEOResultStatus status = IEOResult.IEOResultStatus.SUCCESS;
        IEOResult ieoResult = IEOResult.builder()
                .claimId(ieoClaim.getId())
                .ieoId(ieoClaim.getIeoId())
                .availableAmount(availableAmount)
                .status(status)
                .build();
        if (firstTransaction) {
            ieoResult.setAvailableAmount(ieoDetails.getAmount());
            ieoResult.setClaimId(-1);
            log.error(">>>>>>>>>>> IEO: RESULT {}", ieoResult);
        } else {
            if (!walletService.performIeoTransfer(ieoClaim)) {
                status = IEOResult.IEOResultStatus.FAILED;
            }
            log.error(">>>>>>>>>>> IEO: TRANSFER STATUS {}", status);
            ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), status);
        }
        ieoResultRepository.save(ieoResult);
        ieoDetailsRepository.updateAvailableAmount(ieoClaim.getIeoId(), availableAmount);
        ieoDetails.setAvailableAmount(availableAmount);
        log.error(">>>>>>>>>>> IEO: END of PROCESSING CLAIM #{} AND DETAILS: {}", ieoClaim.getId(), ieoDetails);
        CompletableFuture.runAsync(() -> sendNotifications(ieoClaim.getCreatorEmail(), ieoDetails, "Hello backend!"));
    }

    private void sendNotifications(String userEmail, IEODetails ieoDetails, Object notificationObject) {
        try {
            if (StringUtils.isNotEmpty(userEmail)) {
                String payload = objectMapper.writeValueAsString(new WsMessageObject(WsMessageTypeEnum.IEO, notificationObject));
                stompMessenger.sendPersonalMessageToUser(userEmail, payload);
            }
        } catch (Exception e) {
            /*ignore*/
        }
        try {
            if(StringUtils.isNotEmpty(userEmail)) {
                stompMessenger.sendPersonalDetailsIeo(userEmail, objectMapper.writeValueAsString(ImmutableList.of(ieoDetails)));
            }
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
