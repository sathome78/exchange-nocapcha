package me.exrates.service.stomp;

/**
 * Created by Maks on 24.08.2017.
 */
public interface StompMessenger {
    void sendMessage(String destination, String message);
}
