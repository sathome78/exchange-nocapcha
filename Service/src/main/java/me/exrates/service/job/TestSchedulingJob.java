package me.exrates.service.job;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Log4j2(topic = "test")
public class TestSchedulingJob {

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void foo() {
        log.debug("Executing scheduled method at " + System.currentTimeMillis());
    }
}
