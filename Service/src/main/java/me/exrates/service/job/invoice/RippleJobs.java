package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.RefillService;
import me.exrates.service.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by maks on 16.05.2017.
 */

@Service
@Log4j2
public class RippleJobs {

    @Autowired
    private WithdrawService withdrawService;

    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 5)
    private void checkWithdrawals() {

    }

    private void checkWithdraw(int id) {


        boolean declined = false;
        if(declined) {
            withdrawService.rejectToReview(id);
        }
    }
}
