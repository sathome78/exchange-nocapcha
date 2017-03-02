package me.exrates.model.dto.dataTable;

import me.exrates.model.exceptions.IllegalColumnNameException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by OLEG on 21.02.2017.
 */
public class DataTableParams {

    private int draw;
    private int orderColumn;
    private OrderDirection orderDirection;
    private int start;
    private int length;
    private String searchValue;
    private List<String> columns;


    enum OrderDirection {
        ASC, DESC
    }

    private DataTableParams() {
    }

    public int getDraw() {
        return draw;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public List<String> getColumns() {
        return columns;
    }

    public static DataTableParams resolveParamsFromRequest(Map<String, String> requestParams) {
        DataTableParams dataTableParams = new DataTableParams();
        dataTableParams.draw = Integer.parseInt(requestParams.getOrDefault("draw", "1"));
        dataTableParams.orderColumn = Integer.parseInt(requestParams.getOrDefault("order[0][column]", "0"));
        dataTableParams.orderDirection = OrderDirection.valueOf(requestParams.getOrDefault("order[0][dir]", "asc").toUpperCase());
        dataTableParams.start = Integer.parseInt(requestParams.getOrDefault("start", "0"));
        dataTableParams.length = Integer.parseInt(requestParams.getOrDefault("length", "0"));
        dataTableParams.searchValue = requestParams.getOrDefault("search[value]", "");
        List<String> columnNames = requestParams.entrySet().stream()
                .filter(entry -> entry.getKey().matches("^columns(.+)name\\]$"))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        validateColumnNames(columnNames);
        dataTableParams.columns = columnNames;
        return dataTableParams;
    }

    public String getOrderAndDirection() {
        if (columns.isEmpty() || )

        return columns.get(orderColumn);
    }

    private static void validateColumnNames(List<String> columnNames) {
        if (!columnNames.stream().allMatch(Pattern.compile("^[a-zA-z]+([_.]{1}[a-zA-z]+)*[a-zA-z]$").asPredicate())) {
            throw new IllegalColumnNameException("Illegal column name!");
        }
    }

    @Override
    public String toString() {
        return "DataTableParams{" +
                "draw=" + draw +
                ", orderColumn=" + orderColumn +
                ", orderDirection=" + orderDirection +
                ", start=" + start +
                ", length=" + length +
                ", searchValue='" + searchValue + '\'' +
                ", columns=" + columns +
                '}';
    }
}
