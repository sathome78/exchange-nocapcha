package me.exrates.model.dto.filterData;

import lombok.*;

/**
 * Created by OLEG on 28.02.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FilterDataItem {
    private String name;
    private String sqlClause;
    private Object value;
}
