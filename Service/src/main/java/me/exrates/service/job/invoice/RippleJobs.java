package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.WithdrawService;
import me.exrates.service.ripple.RippleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by maks on 16.05.2017.
 */

@Service
@Log4j2
public class RippleJobs {

    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RippleService rippleService;

    private final static ExecutorService ordersExecutors = Executors.newFixedThreadPool(5);

    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 5)
    private void checkWithdrawals() {
        Merchant merchant = merchantService.findByName("XRP");
        List<WithdrawRequestFlatDto> dtos = withdrawService.getRequestsByMerchantIdAndStatus(merchant.getId(),
                Collections.singletonList(WithdrawStatusEnum.ON_BCH_EXAM.getCode()));
        if (dtos != null && !dtos.isEmpty()) {
            dtos.forEach(p-> ordersExecutors.execute(new Runnable() {
                @Override
                public void run() {
                    checkWithdraw(p.getId(), p.getTransactionHash());
                }
            }));
        }
    }

    private void checkWithdraw(int id, String hash) {
        try {
            boolean checked = rippleService.checkSendedTransaction(hash);
            if (checked) {
               withdrawService.finalizePostWithdrawalRequest(id);
            }
        } catch (Exception e) {
            log.error("xrp transaction error " + e);
            withdrawService.rejectToReview(id);
        }

    }
}
