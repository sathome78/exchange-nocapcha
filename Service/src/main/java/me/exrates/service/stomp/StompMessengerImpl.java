package me.exrates.service.stomp;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;

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

   @Override
   public void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType){
       String message = orderService.getOrdersForRefresh(pairId, operationType);
       sendMessage("/app/topic.trade_orders.".concat(pairId.toString()), message);
   }

   private void sendMessage(String destination, String message) {
       log.debug("send to {}, {}", destination, message);
       messagingTemplate.convertAndSend(destination, message);
   }
}
