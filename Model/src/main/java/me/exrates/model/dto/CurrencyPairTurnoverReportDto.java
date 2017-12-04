package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;

import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
public class CurrencyPairTurnoverReportDto {
    private Integer orderNum;
    private String currencyPairName;
    private OperationType operationType;
    private Integer quantity;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;


    public static String getTitle() {
        return Stream.of("No.", "currency_pair", "operation_type", "quantity",
                "amount_base", "amount_convert")
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.of(String.valueOf(orderNum), currencyPairName, operationType.name(), String.valueOf(quantity),
                BigDecimalProcessing.formatNoneComma(amountBase, false),
                BigDecimalProcessing.formatNoneComma(amountConvert, false))
                .collect(Collectors.joining(";", "", "\r\n"));
    }

}
