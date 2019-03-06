package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TransactionFilterDataDto {

    private String email;
    private Integer offset;
    private Integer limit;
    private int currencyId;
    private String currencyName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private List<Integer> operationTypes;
}
