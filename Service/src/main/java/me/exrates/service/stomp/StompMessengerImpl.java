package me.exrates.service.stomp;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.QRLoginEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.UserDestinationMessageHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Maks on 24.08.2017.
 */
@Log4j2(topic = "ws_stomp_log")
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


    private final List<BackDealInterval> intervals = Arrays.stream(ChartPeriodsEnum.values())
                                                    .map(ChartPeriodsEnum::getBackDealInterval)
                                                    .collect(Collectors.toList());




   @Override
   public void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType){
       String message = orderService.getOrdersForRefresh(pairId, operationType, null);
       sendMessageToDestination("/app/trade_orders/".concat(pairId.toString()), message);
       sendRefreshTradeOrdersMessageToFiltered(pairId, operationType);
   }

   private void sendRefreshTradeOrdersMessageToFiltered(Integer pairId, OperationType operationType) {
      Set<SimpSubscription> subscriptions =
              findSubscribersByDestination("/user/queue/trade_orders/f/".concat(pairId.toString()));
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
                  sendMessageToSubscription(subscription, message, "/queue/trade_orders/f/".concat(pairId.toString()));
              }
          });
      }
   }

   @Override
   public void sendMyTradesToUser(int userId, Integer currencyPair) {
       String userEmail = userService.getEmailById(userId);
       String destination = "/queue/personal/".concat(currencyPair.toString());
       String message = orderService.getTradesForRefresh(currencyPair, userEmail, RefreshObjectsEnum.MY_TRADES);
       log.debug("send my trades to {}, {}", userEmail, destination);
       messagingTemplate.convertAndSendToUser(userEmail, destination, message);
   }

    @Override
    public void sendAllTrades(Integer currencyPair) {
        String destination = "/app/trades/".concat(currencyPair.toString());
        String message = orderService.getTradesForRefresh(currencyPair, null, RefreshObjectsEnum.ALL_TRADES);
        sendMessageToDestination(destination, message);
    }

    @Override
    public void sendChartData(Integer currencyPairId) {
        intervals.forEach(p-> {
            String message = orderService.getChartData(currencyPairId, p);
            String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(p.getInterval()));
            sendMessageToDestination(destination, message);
        });
    }

    @Synchronized
    @Override
    public void sendStatisticMessage(List<Integer> currenciesIds) {
       sendMessageToDestination("/app/statistics", orderService.getSomeCurrencyStatForRefresh(currenciesIds));
    }

    @Override
    public void sendEventMessage(String sessionId, String message) {
        sendMessageToDestination("/app/ev/".concat(sessionId), message);
    }


    private Set<SimpSubscription> findSubscribersByDestination(String destination) {
       return registry.findSubscriptions(subscription -> subscription.getDestination().equals(destination));
   }

   private void sendMessageToDestination(String destination, String message) {
       log.debug("send to {}, {}", destination, message);
       messagingTemplate.convertAndSend(destination, message);
   }

   private void sendMessageToSubscription(SimpSubscription subscription, String message, String dest) {
       log.debug("suscr {}, dest {}", subscription.getSession().getUser().getName(), dest);
       sendMessageToDestinationAndUser(subscription.getSession().getUser().getName(), dest, message);
   }

    private void sendMessageToDestinationAndUser(String user, String destination, String message) {
       log.debug("send to {}, {}", user, destination);
       messagingTemplate.convertAndSendToUser(user,
                                               destination,
                                               message);
    }


    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }


}
