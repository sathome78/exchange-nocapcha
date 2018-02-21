package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class UserRoleBalanceDto {
    private String currency;
    private UserRole userRole;
    private BigDecimal totalBalance;
}
