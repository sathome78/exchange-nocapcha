package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.dto.onlineTableDto.OrderListDto;

import java.util.List;

/**
 * Created by maks on 02.08.2017.
 */
@Data
public class OrdersListWrapper {

    private Object data;
    private String type;
    private int currencyPairId;


    public OrdersListWrapper(Object data, String type, int currencyPairId) {
        this.data = data;
        this.type = type;
        this.currencyPairId = currencyPairId;
    }

    public OrdersListWrapper(Object data, String type) {
        this.data = data;
        this.type = type;
    }
}