package me.exrates.controller;

import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public String tiker(@RequestParam(required = false) String currencyPair) {
        long before = System.currentTimeMillis();
        try {
            if (currencyPair != null) {
                currencyPair = currencyPair.replace('_', '/');
            }
//            return "{\"ERROR\":\"temporary off\"}";
            List<CoinmarketApiDto> list = orderService.getCoinmarketData(currencyPair, new BackDealInterval("24 HOUR"));
            return list.toString().replaceAll("\\[", "{").replaceAll("]", "}");
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }
}
