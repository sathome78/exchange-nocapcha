//package me.exrates.service.cache;
//
//import me.exrates.model.ExOrder;
//import me.exrates.model.dto.StatisticForMarket;
//import me.exrates.model.enums.TradeMarket;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//public interface MarketRatesHolder {
//
//    List<StatisticForMarket> getAll();
//
//    void setRateMarket(ExOrder exOrder);
//
//    List<StatisticForMarket> getStatisticForMarketsByIds(List<Integer> ids);
//
//    StatisticForMarket getOne(Integer id);
//
//    BigDecimal getBtcUsdRate();
//
//    Map<Integer, String> getRatesForMarket(TradeMarket market);
//
//}
