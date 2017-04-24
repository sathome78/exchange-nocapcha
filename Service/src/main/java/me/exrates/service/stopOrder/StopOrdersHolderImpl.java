package me.exrates.service.stopOrder;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.enums.OrderType;
import me.exrates.service.CurrencyService;
import me.exrates.service.StopOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maks on 22.04.2017.
 */
@Log4j2
@Component
public class StopOrdersHolderImpl implements StopOrdersHolder {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private StopOrderService stopOrderService;

    /*contains: integer - currency pair id; ConcurrentSkipListSet - set with orders for this pair*/
    private Map<Integer, ConcurrentSkipListSet<StopOrderSummaryDto>> sellOrdersMap = new ConcurrentHashMap<>();
    private Map<Integer, ConcurrentSkipListSet<StopOrderSummaryDto>> buyOrdersMap = new ConcurrentHashMap<>();

    private Comparator<StopOrderSummaryDto> comparator = Comparator.comparing(StopOrderSummaryDto::getStopRate)
            .thenComparing(StopOrderSummaryDto::getOrderId);

    /*----methods-----*/

    @PostConstruct
    public void init() {
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        currencyPairs.forEach(p->{
            sellOrdersMap.put(p.getId(), new ConcurrentSkipListSet<StopOrderSummaryDto>(comparator));
            buyOrdersMap.put(p.getId(), new ConcurrentSkipListSet<StopOrderSummaryDto>(comparator));
        });
    }

    /**
     * return set with orders by this currency pair @pairId pair which has higher or equal @rate*/

    @Override
    public NavigableSet<StopOrderSummaryDto> getSellOrdersForPairAndStopRate(int pairId, BigDecimal rate) {
        if (!sellOrdersMap.containsKey(pairId)) {
            addNewPairToMap(sellOrdersMap, pairId);
            return Collections.emptyNavigableSet();
        }
        ConcurrentSkipListSet<StopOrderSummaryDto> thisOrdersSet = sellOrdersMap.get(pairId);
        return thisOrdersSet.tailSet(new StopOrderSummaryDto(0, rate), true);
    }

    /**
     * return set with orders by this currency pair @pairId pair which has lower or equal @rate*/
    @Override
    public NavigableSet<StopOrderSummaryDto> getBuyOrdersForPairAndStopRate(int pairId, BigDecimal rate) {
        if (!buyOrdersMap.containsKey(pairId)) {
            addNewPairToMap(buyOrdersMap, pairId);
            return Collections.emptyNavigableSet();
        }
        ConcurrentSkipListSet<StopOrderSummaryDto> thisOrdersSet = buyOrdersMap.get(pairId);
        return thisOrdersSet.headSet(new StopOrderSummaryDto(thisOrdersSet.size(), rate), true);
    }

    @Override
    public void delete(int pairId, StopOrderSummaryDto summaryDto) {
        ConcurrentSkipListSet<StopOrderSummaryDto> thisOrdersSet;
        switch (summaryDto.getOperationType()) {
            case BUY: {
                thisOrdersSet = buyOrdersMap.get(pairId);
                break;
            }
            case SELL: {
                thisOrdersSet = sellOrdersMap.get(pairId);
                break;
            }
            default: {
                throw new RuntimeException("map not conatins this order! ".concat(summaryDto.toString()));
            }
        }
        if (!thisOrdersSet.contains(summaryDto)) {;
            throw new RuntimeException("map not conatins this order! ".concat(summaryDto.toString()));
        }
        thisOrdersSet.remove(summaryDto);
    }

    @Override
    public void addOrder(ExOrder exOrder) {
        switch (exOrder.getOperationType()) {
            case BUY: {
                buyOrdersMap.get(exOrder.getCurrencyPairId())
                        .add(new StopOrderSummaryDto(exOrder.getId(), exOrder.getExRate(), exOrder.getOperationType()));
                break;
            }
            case SELL: {
                sellOrdersMap.get(exOrder.getCurrencyPairId())
                        .add(new StopOrderSummaryDto(exOrder.getId(), exOrder.getExRate(), exOrder.getOperationType()));
                break;
            }
        }
    }

    private void addNewPairToMap(Map<Integer, ConcurrentSkipListSet<StopOrderSummaryDto>> map, Integer currencyPairId) {
        map.put(currencyPairId, new ConcurrentSkipListSet<StopOrderSummaryDto>());
    }




}
