package me.exrates.model.dto.filterData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by OLEG on 28.02.2017.
 */
public abstract class TableFilterData {
    private List<FilterDataItem> filterItems;

    public abstract void initFilterItems();

    void populateFilterItemsNonEmpty(FilterDataItem[] items) {
        filterItems = Stream.of(items).filter(item -> !(item.getValue() == null || String.valueOf(item.getValue()).isEmpty()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getNamedParams() {
        return filterItems.stream().collect(Collectors.toMap(FilterDataItem::getName, FilterDataItem::getValue));
    }

    public String getSQLFilterClause() {
        return filterItems.stream().map(item -> item.getSqlClause().concat(" ").concat(item.formatParamForSql()))
                .collect(Collectors.joining(" AND "));
    }

}
