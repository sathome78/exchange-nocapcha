package me.exrates.service.stomp;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.*;

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
    private DefaultSimpUserRegistry registry;
    @Autowired
    private UserService userService;



   @Override
   public void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType){
       String message = orderService.getOrdersForRefresh(pairId, operationType, null);
       sendMessageToDestination("/app/topic.trade_orders.".concat(pairId.toString()), message);
       sendRefreshTradeOrdersMessageToFiltered(pairId, operationType);
   }

   private void sendRefreshTradeOrdersMessageToFiltered(Integer pairId, OperationType operationType) {
      Set<SimpSubscription> subscriptions =
              findSubscribersByDestination("/app/topic.trade_orders.f.concat(pairId.toString())");
      if (!subscriptions.isEmpty()) {
          Map<UserRole, List<SimpSubscription>> map = new HashMap<>();
          subscriptions.forEach(p -> {
              String userEmail = p.getSession().getUser().getName();
              if (!StringUtils.isEmpty(userEmail)) {
                  UserRole role = userService.getUserRoleFromDB(userEmail);
                  if (map.containsKey(role)) {
                      map.get(role).add(p);
                  } else {
                      map.put(role, new ArrayList<SimpSubscription>(){{add(p);}});
                  }
              }
          });
          map.forEach((k,v) -> {
              String message = orderService.getOrdersForRefresh(pairId, operationType, k);
              for (SimpSubscription subscription : v) {
                  sendMessageToSubscription(subscription, message);
              }
          });
      }
   }

   @Override
   public void sendMyTradesToUser(String userEmail, Integer currencyPair) {
       String destination = "/app/topic.myTrades.".concat(currencyPair.toString());
       String message = orderService.getTradesForRefresh(currencyPair, userEmail, RefreshObjectsEnum.MY_TRADES);
       sendMessageToDestinationAndUser(userEmail, destination, message);
   }

    @Override
    public void sendAllTradesToUser(Integer currencyPair) {
        String destination = "/app/topic.trades_charts.".concat(currencyPair.toString());
        String message = orderService.getTradesForRefresh(currencyPair, null, RefreshObjectsEnum.ALL_TRADES);
        sendMessageToDestination(destination, message);
    }

   private Set<SimpSubscription> findSubscribersByDestination(String destination) {
       return registry.findSubscriptions(new SimpSubscriptionMatcher() {
           @Override
           public boolean match(SimpSubscription subscription) {
               return subscription.getDestination().equals(destination);
           }
       });
   }


   private void sendMessageToDestination(String destination, String message) {
       log.debug("send to {}, {}", destination, message);
       messagingTemplate.convertAndSend(destination, message);
   }

   private void sendMessageToSubscription(SimpSubscription subscription, String message) {
       sendMessageToDestinationAndUser(subscription.getSession().getUser().getName(), subscription.getDestination(), message);
   }

    private void sendMessageToDestinationAndUser(String user, String destination, String message) {
        messagingTemplate.convertAndSendToUser(user,
                                               destination,
                                               message);
    }
}
