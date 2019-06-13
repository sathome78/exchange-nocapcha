//package me.exrates.service.cache;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import lombok.extern.log4j.Log4j2;
//import me.exrates.dao.OrderDao;
//import me.exrates.model.CurrencyPair;
//import me.exrates.model.ExOrder;
//import me.exrates.model.dto.ExOrderStatisticsDto;
//import me.exrates.model.dto.StatisticForMarket;
//import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
//import me.exrates.model.enums.ActionType;
//import me.exrates.model.enums.ChartPeriodsEnum;
//import me.exrates.model.enums.TradeMarket;
//import me.exrates.model.util.BigDecimalProcessing;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@Log4j2
//@EnableScheduling
//@Service
//public class MarketRatesHolderImpl implements MarketRatesHolder {
//
//
//    private static final Cache<Integer, ExOrderStatisticsShortByPairsDto> LOCAL_CACHE = CacheBuilder.newBuilder()
//            .expireAfterWrite(1, TimeUnit.MINUTES)
//            .maximumSize(1000L)
//            .build();
//
//    private static Integer ETH_USD_ID = 0;
//    private static Integer BTC_USD_ID = 0;
//    private final OrderDao orderDao;
//    private final ExchangeRatesRedisRepository redisRepository;
//
//    @Autowired
//    public MarketRatesHolderImpl(OrderDao orderDao,
//                                 ExchangeRatesRedisRepository redisRepository) {
//        this.orderDao = orderDao;
//        this.redisRepository = redisRepository;
//    }
//
//    @Override
//    public BigDecimal getBtcUsdRate() {
//        StatisticForMarket dto = getOne(BTC_USD_ID);
//        return dto == null ? BigDecimal.ZERO : dto.getLastOrderRate();
//    }
//
//    @Override
//    public Map<Integer, String> getRatesForMarket(TradeMarket market) {
//        return null;
//    }
//
//    @Override
//    public StatisticForMarket getOne(Integer id) {
//        ExOrderStatisticsShortByPairsDto statisticDto = LOCAL_CACHE.getIfPresent(id);
//        if (statisticDto == null) {
//            updateLocalCache();
//            statisticDto = redisRepository.get(id);
//        }
//        return transform(statisticDto);
//    }
//
//    @Override
//    public List<StatisticForMarket> getAll() {
//        if (LOCAL_CACHE.size() == 0) {
//            updateLocalCache();
//        }
//        return LOCAL_CACHE.asMap().values().stream()
//                .map(this::transform)
//                .peek(this::calculatePriceInUSD)
//                .peek(this::calculateCurrencyVolume)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void setRateMarket(ExOrder exOrder) {
//        this.setRatesMarketMap(exOrder.getCurrencyPairId(), exOrder.getExRate(), exOrder.getAmountBase());
//    }
//
//    @Override
//    public List<StatisticForMarket> getStatisticForMarketsByIds(List<Integer> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        if (LOCAL_CACHE.size() == 0) {
//            updateLocalCache();
//        }
//
//        return LOCAL_CACHE.getAllPresent(ids).values()
//                .stream().map(this::transform)
//                .peek(p -> {
//                    this.calculatePriceInUSD(p);
//                    this.calculateCurrencyVolume(p);
//                }).collect(Collectors.toList());
//    }
//
//    private synchronized void setRatesMarketMap(int currencyPairId, BigDecimal rate, BigDecimal amount) {
//        LOCAL_CACHE.invalidateAll();
//        if (redisRepository.exist(currencyPairId)) {
//
//            CurrencyPair currencyPair = new CurrencyPair();
//            currencyPair.setId(currencyPairId);
//
//            ExOrderStatisticsDto statistic = orderDao.getOrderStatistic(currencyPair, ChartPeriodsEnum.HOURS_24.getBackDealInterval());
//
//            StatisticForMarket statisticForMarket = transform(redisRepository.get(currencyPairId));
//            statisticForMarket.setLastOrderRate(rate);
//            BigDecimal predLastRate = new BigDecimal(statistic.getFirstOrderRate());
//            statisticForMarket.setPredLastOrderRate(BigDecimalProcessing.normalize(predLastRate));
//            BigDecimal volume = BigDecimalProcessing.doAction(statisticForMarket.getVolume(), amount, ActionType.ADD);
//            statisticForMarket.setVolume(volume);
//            this.processPercentChange(statisticForMarket);
//            this.calculatePriceInUSD(statisticForMarket);
//            redisRepository.update(transform(statisticForMarket));
//        }
//    }
//
//    private void calculatePriceInUSD(StatisticForMarket pair) {
//        if (pair.getMarket().equalsIgnoreCase("USD")) {
//            pair.setPriceInUsd(pair.getLastOrderRate());
//            return;
//        }
//        BigDecimal dealPrice = pair.getLastOrderRate();
//        if (pair.getMarket().equalsIgnoreCase("BTC")) {
//            pair.setPriceInUsd(getPriceInUsd(dealPrice, BTC_USD_ID));
//        } else if (pair.getMarket().equalsIgnoreCase("ETH")) {
//            pair.setPriceInUsd(getPriceInUsd(dealPrice, ETH_USD_ID));
//        }
//    }
//
//    private BigDecimal getPriceInUsd(BigDecimal dealPrice, int pairId) {
//        if (pairId == 0) {
//            return null;
//        } else if (redisRepository.exist(pairId)) {
//            String lastOrderRate = redisRepository.get(pairId).getLastOrderRate();
//            BigDecimal lastRateForBtcUsd = new BigDecimal(lastOrderRate);
//            return lastRateForBtcUsd.multiply(dealPrice);
//        }
//        return null;
//    }
//
//    private void processPercentChange(StatisticForMarket o) {
//        BigDecimal lastExrate = o.getLastOrderRate();
//        BigDecimal predLast = o.getPredLastOrderRate() != null ? o.getPredLastOrderRate() : BigDecimal.ZERO;
//        BigDecimal percentChange = BigDecimal.ZERO;
//        if (BigDecimalProcessing.moreThanZero(lastExrate) && BigDecimalProcessing.moreThanZero(predLast)) {
//            percentChange = BigDecimalProcessing.doAction(predLast, lastExrate, ActionType.PERCENT_GROWTH);
//        }
//        o.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, Locale.ENGLISH, 2));
//    }
//
//    private void calculateCurrencyVolume(StatisticForMarket statisticForMarket) {
//        BigDecimal lastOrderRate = statisticForMarket.getLastOrderRate();
//        BigDecimal volume = statisticForMarket.getVolume();
//        BigDecimal currencyVolume = BigDecimalProcessing.doAction(volume, lastOrderRate, ActionType.MULTIPLY);
//        statisticForMarket.setCurrencyVolume(currencyVolume);
//    }
//
//    private StatisticForMarket transform(ExOrderStatisticsShortByPairsDto dto) {
//        StatisticForMarket statisticForMarket = new StatisticForMarket();
//        statisticForMarket.setCurrencyPairId(dto.getCurrencyPairId());
//        statisticForMarket.setCurrencyPairName(dto.getCurrencyPairName());
//        statisticForMarket.setVolume(new BigDecimal(dto.getVolume()));
//        statisticForMarket.setLastOrderRate(new BigDecimal(dto.getLastOrderRate()));
//        statisticForMarket.setPredLastOrderRate(new BigDecimal(dto.getPredLastOrderRate()));
//        statisticForMarket.setType(dto.getType());
//        statisticForMarket.setNeedToRefresh(dto.isNeedRefresh());
//        statisticForMarket.setPage(dto.getPage());
//        if (dto.getPriceInUSD() != null) statisticForMarket.setPriceInUsd(new BigDecimal(dto.getPriceInUSD()));
//        return statisticForMarket;
//    }
//
//    private ExOrderStatisticsShortByPairsDto transform(StatisticForMarket statistic) {
//        ExOrderStatisticsShortByPairsDto dto = new ExOrderStatisticsShortByPairsDto();
//        dto.setCurrency1Id(statistic.getCurrencyPairId());
//        dto.setCurrencyPairName(statistic.getCurrencyPairName());
//        dto.setMarket(statistic.getMarket());
//        dto.setLastOrderRate(statistic.getLastOrderRate().toPlainString());
//        dto.setPredLastOrderRate(statistic.getPredLastOrderRate().toPlainString());
//        dto.setPercentChange(statistic.getPercentChange());
//        dto.setPriceInUSD(statistic.getPriceInUsd().toPlainString());
//        dto.setVolume(statistic.getVolume().toPlainString());
//        dto.setNeedRefresh(statistic.isNeedToRefresh());
//        dto.setType(statistic.getType());
//        return dto;
//    }
//
//    private void updateLocalCache() {
//        redisRepository.getAll().forEach(o -> {
//            LOCAL_CACHE.put(o.getCurrencyPairId(), o);
//            if (o.getCurrencyPairName().equalsIgnoreCase("BTC/USD")) BTC_USD_ID = o.getCurrencyPairId();
//            if (o.getCurrencyPairName().equalsIgnoreCase("ETH/USD")) ETH_USD_ID = o.getCurrencyPairId();
//        });
//    }
//}
