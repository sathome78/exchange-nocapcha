package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter @Setter
@AllArgsConstructor
public class UserRoleTotalBalancesReportDto<T extends Enum> {

    private String currency;
    private Map<String, BigDecimal> balances;
    private Class<T> enumClass;



    public static <T extends Enum> String getTitle(Class<T> enumClass) {
        return Stream.concat(Stream.of("currency"), Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).sorted())
                .collect(Collectors.joining(";", "", "\r\n"));
    }


    @Override
    public String toString() {
        return Stream.concat(Stream.of(currency), Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).sorted()
                .map(item -> BigDecimalProcessing.formatNoneComma(balances.getOrDefault(item, BigDecimal.ZERO), false)))
                .collect(Collectors.joining(";", "", "\r\n"));
    }


}
