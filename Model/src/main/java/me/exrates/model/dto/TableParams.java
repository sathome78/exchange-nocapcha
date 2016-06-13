package me.exrates.model.dto;

import me.exrates.model.enums.OrderStatus;

/**
 * Created by Valk on 06.06.2016.
 */
public class TableParams {
    /*params for any tables*/
    private String tableId;
    private Integer pageSize;
    /**/
    /*params for concrete tables*/

    /*getters setters*/

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
