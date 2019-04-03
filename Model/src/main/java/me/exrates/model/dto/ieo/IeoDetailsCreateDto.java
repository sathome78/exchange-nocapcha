package me.exrates.model.dto.ieo;

import lombok.Data;

import java.math.BigDecimal;


public class IeoDetailsCreateDto {

    private String currencyDescription;
    private String priceString;
    private BigDecimal availableBalance;

}
