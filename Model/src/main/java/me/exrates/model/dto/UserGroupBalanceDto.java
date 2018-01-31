package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.ReportGroupUserRole;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class UserGroupBalanceDto {
    private String currency;
    private ReportGroupUserRole reportGroupUserRole;
    private BigDecimal totalBalance;
}
