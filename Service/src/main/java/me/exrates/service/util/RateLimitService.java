package me.exrates.service.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.exrates.model.enums.OperationType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maks on 20.06.2017.
 */
@Service
public class RateLimitService  {


    private static final int TIME_LIMIT_SECONDS = 3600;

    private static final int ATTEMPS = 5;

    private Map<String, CopyOnWriteArrayList<LocalDateTime>> map = new ConcurrentHashMap<>();

    @Scheduled(cron = "* 5 * * * *")
    public void clearExpiredRequests() {
        new HashMap<>(map).forEach((k, v)-> {
            if (v.stream().filter(p->p.isAfter(LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS))).count() == 0) {
                map.remove(k);
            }
        });
    }


    public boolean registerRequestAndCheck(String userEmail) {
        map.putIfAbsent(userEmail, new CopyOnWriteArrayList<>());
        map.get(userEmail).add(LocalDateTime.now());
        return checkLimitsExceed(userEmail);
    }


    private boolean checkLimitsExceed(String email) {
        LocalDateTime beginTime = LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS);
        long counter = map.get(email).stream().filter(p->p.isAfter(beginTime)).count();
        return counter < ATTEMPS;
    }

}