package me.exrates.service.stomp;

import lombok.Data;

/**
 * Created by Maks on 25.08.2017.
 */
@Data
public class OrdersMessage {

    private String destination;

    private String message;

    public OrdersMessage(String destination, String message) {
        this.destination = destination;
        this.message = message;
    }
}
