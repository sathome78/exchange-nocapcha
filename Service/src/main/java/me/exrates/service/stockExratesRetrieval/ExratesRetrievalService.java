package me.exrates.service.stockExratesRetrieval;

import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 09.02.2017.
 */
@Service
public class ExratesRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private OrderService orderService;

    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        return orderService.getCoinmarketDataForActivePairs("", new BackDealInterval("24 HOUR"))
                .stream()
                .map(dto -> {
                    StockExchangeStats stockExchangeStats = new StockExchangeStats();
                    stockExchangeStats.setStockExchange(stockExchange);
                    stockExchangeStats.setCurrencyPairId(dto.getCurrencyPairId());
                    stockExchangeStats.setDate(LocalDateTime.now());
                    stockExchangeStats.setPriceLast(dto.getLast());
                    stockExchangeStats.setPriceBuy(dto.getHighestBid());
                    stockExchangeStats.setPriceSell(dto.getLowestAsk());
                    stockExchangeStats.setPriceLow(dto.getLow24hr());
                    stockExchangeStats.setPriceHigh(dto.getHigh24hr());
                    stockExchangeStats.setVolume(dto.getBaseVolume());
                    return stockExchangeStats;
                }).collect(Collectors.toList());
    }

    @Override
    public String getStockExchangeName() {
        return "Exrates";
    }
}
