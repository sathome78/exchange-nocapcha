package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author ValkSam
 */
@Getter @Setter
public class WalletsForOrderCancelDto {
    int orderId;
    int orderStatusId;
    BigDecimal reservedAmount;
    int walletId;
    BigDecimal activeBalance;
    BigDecimal reservedBalance;
}
