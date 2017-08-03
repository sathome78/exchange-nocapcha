package me.exrates.service.encoders;

import com.google.gson.Gson;
import me.exrates.model.dto.OrdersListWrapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Created by maks on 02.08.2017.
 */
public class MessageDecoder implements Decoder.Text<OrdersListWrapper> {

    private static Gson gson = new Gson();

    @Override
    public OrdersListWrapper decode(String s) throws DecodeException {
        return gson.fromJson(s, OrdersListWrapper.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
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
