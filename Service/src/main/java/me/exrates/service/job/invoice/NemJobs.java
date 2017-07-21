package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.MerchantService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.NemTransactionException;
import me.exrates.service.exception.RippleCheckConsensusException;
import me.exrates.service.nem.NemService;
import me.exrates.service.ripple.RippleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by maks on 20.07.2017.
 */
@Log4j2
@Service
public class NemJobs {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private NemService nemService;

    private static final String NEM_MERCHANT = "NEM";

    private final static ExecutorService ordersExecutors = Executors.newFixedThreadPool(5);

    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 5)
    private void checkWithdrawals() {
        Merchant merchant = merchantService.findByName(NEM_MERCHANT);
        List<WithdrawRequestFlatDto> dtos = withdrawService.getRequestsByMerchantIdAndStatus(merchant.getId(),
                Collections.singletonList(WithdrawStatusEnum.ON_BCH_EXAM.getCode()));
        if (dtos != null && !dtos.isEmpty()) {
            dtos.forEach(p-> ordersExecutors.execute(() -> checkWithdraw(p.getId(), p.getTransactionHash(), p.getAdditionalParams())));
        }
    }

    private void checkWithdraw(int id, String hash, String additionalParams) {
        try {
            boolean checked = nemService.checkSendedTransaction(hash, additionalParams);
            if (checked) {
                withdrawService.finalizePostWithdrawalRequest(id);
            }
        } catch (NemTransactionException e) {
            log.error("nem transaction not included in block " + e);
            withdrawService.rejectToReview(id);
        } catch (Exception e) {
            log.error("nem transaction check error, will check it next time " + e);
        }
    }
}
