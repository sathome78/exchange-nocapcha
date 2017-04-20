package me.exrates.model;

import lombok.Data;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by maks on 19.04.2017.
 */

@Data
public class StopOrder {

    private int id;
    private int userId;
    private BigDecimal stop;
    private BigDecimal limit;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;/*nedded?*/
    private BigDecimal comissionFixedamount;
    private int currencyPairId;
    private OperationType operationType;
    private Integer orderId;
    private int orderStatus;
    private LocalDateTime dateCreation;
    private LocalDateTime modificationDate;

}
