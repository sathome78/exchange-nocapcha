package me.exrates.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by maks on 15.03.2017.
 */
public class UserTransferInfoDto {

    private String currencyName;
    private BigDecimal amount;
    private BigDecimal comission;
    private LocalDateTime creationDate;
    private String UserFromEmail;
    private String UserToEmail;

}
