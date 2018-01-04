package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Maks on 29.12.2017.
 */
@Log4j2
@Component
public class ChartsCache {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private OrderService orderService;

    /**Map <pairId, <interval, data>>
     * */
    private Map<Integer, Map<String, String>> cacheMap = new ConcurrentHashMap<>();
    private Map<Integer, Object> locksMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<CurrencyPair> pairs = currencyService.getAllCurrencyPairs();
        pairs.forEach(p-> {
            log.debug("start initialize cache for {}", p.getName());
            updateCache(p.getId());
            log.debug("cache for {} initialized!", p.getName());
        });
    }

    public String getDataForPeriod(Integer pairId, String interval) {
        return getData(pairId).get(interval);
    }


    public Map<String, String> getData(Integer currencyPairId) {
        if (!cacheMap.containsKey(currencyPairId)) {
            updateCache(currencyPairId);
        }
        synchronized (locksMap.get(currencyPairId)) {
            return cacheMap.get(currencyPairId);
        }
    }

    public void updateCache(Integer currencyPairId) {
        synchronized (locksMap.computeIfAbsent(currencyPairId,
                p -> locksMap.put(currencyPairId, new Object()))) {
            Map<String, String> map = cacheMap.computeIfAbsent(currencyPairId,
                    p -> cacheMap.put(currencyPairId, new ConcurrentHashMap<>()));
            orderService.getIntervals().forEach(p -> {
                map.put(p.getInterval(), orderService.getChartData(currencyPairId, p));
            });
        }
    }

}
