package me.exrates.controller;

import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Valk on 11.05.2016.
 */

@RestController
public class PublicController {
    private static final Logger LOGGER = LogManager.getLogger(PublicController.class);

    @Autowired
    OrderService orderService;

    @RequestMapping("/public/coinmarketcap/ticker")
    public String tiker(@RequestParam(required = false) String currencyPair, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        List<CoinmarketApiDto> list = null;
        String ip = "";
        try {
            if (currencyPair != null) {
                currencyPair = currencyPair.replace('_', '/');
            }
            ip = request.getHeader("X-FORWARDED-FOR");
            if (ip == null) {
                ip = request.getRemoteHost();
            }
            list = orderService.getCoinmarketData(currencyPair, new BackDealInterval("24 HOUR"));
            return list.toString().replaceAll("\\[", "{").replaceAll("]", "}");
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... for pair: "+currencyPair+" from ip: " + ip + " ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            StringBuilder stringBuilder = new StringBuilder("\r\n");
            if (list != null) {
                for (CoinmarketApiDto e : list) {
                    stringBuilder
                            .append("     ")
                            .append(e.getCurrency_pair_name())
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getLast(), true))
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getLowestAsk(), true))
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getHighestBid(), true))
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getPercentChange(), true))
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getBaseVolume(), true))
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getQuoteVolume(), true))
                            .append(" ")
                            .append(e.getIsFrozen())
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getHigh24hr(), true))
                            .append(" ")
                            .append(BigDecimalProcessing.formatNonePointQuoted(e.getLow24hr(), true))
                            .append("\r\n");
                }
            }
            LOGGER.debug("completed... from ip: " + ip + " ms: " + (after - before) + stringBuilder.toString());
        }
    }
}
