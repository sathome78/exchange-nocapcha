package me.exrates.service.vo;




import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Maks on 07.09.2017.
 */
@Log4j2
@Component
public class CurrencyStatisticsHandler {

    @Autowired
    private StompMessenger stompMessenger;

    private Set<Integer> currenciesSet = Sets.newConcurrentHashSet();

    private final Semaphore semaphoreMain = new Semaphore(1, true);
    private static final long DELAY = 700;
    private ReentrantLock lock = new ReentrantLock();

    @Async
    public void onEvent(int pairId) {
        try {
            if (lock.isLocked()) {
                log.debug("thr. wait");
                lock.newCondition().await(10, TimeUnit.SECONDS);
            }
            log.debug("add pair {}", pairId);
            currenciesSet.add(pairId);
            if (semaphoreMain.tryAcquire()) {
                log.debug("try to send stat");
                Thread.sleep(DELAY);
                lock.lock();
                log.debug("locked");
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                List<Integer> forUpdate = Lists.newArrayList(currenciesSet);
                log.debug("copied");
                currenciesSet.clear();
                semaphoreMain.release();
                log.debug("semaphore released");
                lock.newCondition().signalAll();
                lock.unlock();
                log.debug("lock unlocked");
                stompMessenger.sendStatisticMessage(forUpdate);
                log.debug("sended");
            }
        } catch (Exception e) {
            log.error(e);
            semaphoreMain.release();
            lock.unlock();
        }
    }


}
