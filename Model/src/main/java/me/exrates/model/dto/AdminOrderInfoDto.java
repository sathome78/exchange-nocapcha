package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.OrderInfoDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by maks on 10.07.2017.
 */
@Getter
@Setter
@ToString
public class AdminOrderInfoDto {

    private boolean isAcceptable;
    private String notification;
    private OrderInfoDto orderInfo;

    public AdminOrderInfoDto(OrderInfoDto orderInfo) {
        this.orderInfo = orderInfo;
    }
}
