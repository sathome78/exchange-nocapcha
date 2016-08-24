package me.exrates.controller;

import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
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
    @Autowired
    OrderService orderService;


    @RequestMapping("/public/coinmarketcap/ticker")
    public String tiker(@RequestParam(required = false) String currencyPair) {
        if (currencyPair != null) {
            currencyPair = currencyPair.replace('_', '/');
        }
        return "{\"ERROR\":\"temporary off\"}";
        /*TODO for monitoring
        List<CoinmarketApiDto1> list = orderService.getCoinmarketData(currencyPair, new BackDealInterval("24 HOUR"));
        return list.toString().replaceAll("\\[", "{").replaceAll("]", "}");*/
    }
}
