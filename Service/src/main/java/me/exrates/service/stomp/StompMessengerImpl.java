package me.exrates.service.stomp;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Maks on 24.08.2017.
 */
@Log4j2
@Component
public class StompMessengerImpl implements StompMessenger{

    @Autowired
    private OrderService orderService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private CurrencyService currencyService;

   /* @Scheduled(fixedDelay = 2000)
    public void refreshOrders() {
        log.debug("update pairs");
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs();
        currencyPairs.forEach(p->{
            String destination = "/app/topic.trade_orders." + p.getId();

        });

    }*/

   @Override
   public void sendMessage(String destination, String message){
       messagingTemplate.convertAndSend(destination, message);
   }
}
