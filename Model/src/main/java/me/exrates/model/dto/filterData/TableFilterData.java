package me.exrates.model.dto.filterData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 28.02.2017.
 */
public abstract class TableFilterData {
    List<FilterDataItem> filterItems;

    public abstract void initFilterItems();

    public Map<String, Object> getNamedParams() {
        return filterItems.stream().collect(Collectors.toMap(FilterDataItem::getName, FilterDataItem::getValue));
    }

    public String getSQLFilterClause() {
        return filterItems.stream().map(item -> item.getSqlClause().concat(resolveParam(item)))
                .collect(Collectors.joining(" AND "));
    }

    private String resolveParam(FilterDataItem dataItem) {
        if (dataItem.getSqlClause().endsWith("IN")) {
            return "(:".concat(dataItem.getName()).concat(")");
        } else {
            return " :".concat(dataItem.getName());
        }
    }

}
