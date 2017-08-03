package me.exrates.service.encoders;

import com.google.gson.Gson;
import me.exrates.model.dto.OrdersListWrapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by maks on 02.08.2017.
 */
public class MessageEncoder implements Encoder.Text<OrdersListWrapper> {

    private static Gson gson = new Gson();

    @Override
    public String encode(OrdersListWrapper message) throws EncodeException {
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}