package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiatPair {

    private int id;
    private String name;
    private int currency1;
    private int currency2;
    private String market;
    private boolean hidden;
}