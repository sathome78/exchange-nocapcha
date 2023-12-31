package me.exrates.service.chart;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.CandleDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@NoArgsConstructor(access = AccessLevel.NONE)
public final class CandleDataConverter {

    public static Map<String, Object> convert(List<CandleDto> data) {
        data.sort(Comparator.comparing(CandleDto::getTime));

        List<Long> t = new ArrayList<>();
        List<BigDecimal> o = new ArrayList<>();
        List<BigDecimal> c = new ArrayList<>();
        List<BigDecimal> h = new ArrayList<>();
        List<BigDecimal> l = new ArrayList<>();
        List<BigDecimal> v = new ArrayList<>();

        data.forEach(candle -> {
            t.add(candle.getTime());
            o.add(candle.getOpen());
            h.add(candle.getHigh());
            l.add(candle.getLow());
            c.add(candle.getClose());
            v.add(candle.getVolume());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("s", "ok");
        response.put("o", o);
        response.put("c", c);
        response.put("h", h);
        response.put("l", l);
        response.put("v", v);
        response.put("t", t);

        return response;
    }
}