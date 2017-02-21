package me.exrates.model.dto.dataTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(int orderColumn) {
        this.orderColumn = orderColumn;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public static DataTableParams resolveParamsFromRequest(Map<String, String> requestParams) {
        DataTableParams dataTableParams = new DataTableParams();
        dataTableParams.draw = Integer.parseInt(requestParams.getOrDefault("draw", "1"));
        dataTableParams.orderColumn = Integer.parseInt(requestParams.getOrDefault("order[0][column]", "0"));
        dataTableParams.orderDirection = OrderDirection.valueOf(requestParams.getOrDefault("order[0][dir]", "asc").toUpperCase());
        dataTableParams.start = Integer.parseInt(requestParams.getOrDefault("start", "0"));
        dataTableParams.length = Integer.parseInt(requestParams.getOrDefault("length", "10"));
        dataTableParams.searchValue = requestParams.getOrDefault("search[value]", "");
        dataTableParams.columns = requestParams.entrySet().stream()
                .filter(entry -> entry.getKey().matches("^columns(.+)name\\]$"))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        return dataTableParams;
    }

    public String getOrderColumnName() {
        return columns.get(orderColumn);
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
