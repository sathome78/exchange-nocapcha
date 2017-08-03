package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.dto.onlineTableDto.OrderListDto;

import java.util.List;

/**
 * Created by maks on 02.08.2017.
 */
@Data
public class OrdersListWrapper {

    private List<OrderListDto> list;
    private String event;
    private int type;

    public OrdersListWrapper(List<OrderListDto> list, String event, int type) {
        this.list = list;
        this.event = event;
        this.type = type;
    }
}
