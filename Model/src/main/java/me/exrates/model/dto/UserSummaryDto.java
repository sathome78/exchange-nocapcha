package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private String registrationIp;
    private LocalDateTime lastEntryDate;
    private String lastIp;
    private String currencyName;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private LocalDateTime lastOrderDate;
    private BigDecimal inputSummary;
    private LocalDateTime lastInputDate;
    private BigDecimal outputSummary;
    private LocalDateTime lastOutputDate;

    public Boolean isEmpty() {
        return (isNull(activeBalance) || activeBalance.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(reservedBalance) || reservedBalance.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(inputSummary) || inputSummary.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(outputSummary) || outputSummary.compareTo(BigDecimal.ZERO) == 0);
    }
}
