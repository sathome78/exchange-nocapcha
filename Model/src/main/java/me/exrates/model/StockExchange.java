package me.exrates.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
public class StockExchange {

    private Integer id;
    private String name;
    private String link;
    private List<CurrencyPair> availableCurrencyPairs = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<CurrencyPair> getAvailableCurrencyPairs() {
        return availableCurrencyPairs;
    }

    public void setAvailableCurrencyPairs(List<CurrencyPair> availableCurrencyPairs) {
        this.availableCurrencyPairs = availableCurrencyPairs;
    }

    @Override
    public String toString() {
        return "StockExchange{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", availableCurrencyPairs=" + availableCurrencyPairs +
                '}';
    }
}
