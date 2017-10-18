package me.exrates.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Maks on 12.10.2017.
 */
@Data
public class NotificatorTotalPriceDto {

    private String messagePrice;
    private String subscriptionPrice;
    private String lookupPrice;
    private String code;
}
