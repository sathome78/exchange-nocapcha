package me.exrates.service.vo;

import jnr.ffi.provider.jffi.NumberUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@EqualsAndHashCode
@Getter
@Setter
public class OrdersEventsHandler {

    @Autowired
    private StompMessenger stompMessenger;

    private Integer pairId;
    private OperationType operationType;

    private AtomicInteger eventsCount = new AtomicInteger(0);
    private AtomicBoolean started = new AtomicBoolean(false);

    private float loadFactor = 1;
    private long refreshTime = 1500; /*in millis*/
    private static final long MIN_REFRESH_TIME = 500;
    private static final long MAX_REFRESH_TIME = 2000;

    private Timer timer;

    private static final int MAX_EVENTS = 3;
    private static final int MIN_EVENTS = 1;
    private int lastEventsCountBeforeSend;


    private OrdersEventsHandler(Integer pairId, OperationType operationType) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.pairId = pairId;
        this.operationType = operationType;
        timer = new Timer();
    }

    public static OrdersEventsHandler init(Integer pairId, OperationType operationType) {
        return new OrdersEventsHandler(pairId, operationType);
    }

    @Synchronized
    public void onOrderEvent() {
        eventsCount.incrementAndGet();
        calculateLoadFactor();
        if (!started.get()) {
            started.set(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    lastEventsCountBeforeSend = eventsCount.get();
                    eventsCount.set(0);
                    started.set(false);
                    stompMessenger.sendRefreshTradeOrdersMessage(pairId, operationType);
                }
            }, getProperlyRefreshTime((long) (loadFactor * refreshTime)));
        }
    }


    private void calculateLoadFactor() {
        float diff = lastEventsCountBeforeSend/MAX_EVENTS;
        setLoadFactor(getLoadFactor() / diff);
    }

    private long getProperlyRefreshTime(long calculatedRefreshTime) {
        return Long.max(Long.min(calculatedRefreshTime, MAX_REFRESH_TIME), MIN_REFRESH_TIME);
    }



}
