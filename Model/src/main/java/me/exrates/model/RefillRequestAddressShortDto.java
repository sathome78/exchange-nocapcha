package me.exrates.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by Maks on 26.12.2017.
 */
@Data
public class RefillRequestAddressShortDto {

    private String userEmail;
    private String address;
    private String addressFieldName;
    private String currencyName;
    private int merchantId;
    private LocalDateTime generationDate;
}
