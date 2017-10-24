package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by Maks on 24.10.2017.
 */
@Data
public class RefillRequestManualDto {

    private String email;
    private int currency;
    private String txHash;
    private String address;
    private BigDecimal amount;
    private OperationType operationType = OperationType.INPUT;
    private Integer merchantId;
}
