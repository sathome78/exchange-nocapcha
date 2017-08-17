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
    private String event;
    private String source;
    private String type;

    public OrdersListWrapper(Object data, String event, String source, String type) {
        this.data = data;
        this.event = event;
        this.source = source;
        this.type = type;
    }
}
