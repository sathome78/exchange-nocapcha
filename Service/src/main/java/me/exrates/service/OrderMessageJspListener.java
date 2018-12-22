package me.exrates.service;

import me.exrates.model.dto.InputCreateOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@EnableRabbit
@Component
public class OrderMessageJspListener {

    static final Logger logger = LoggerFactory.getLogger(OrderMessageJspListener.class);

    /*
    * This method is triggered if rabbit exchange receives message ({order}) from counterpart application
     */
    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
    public void processOrder(InputCreateOrderDto order) {
        // todo update cache or do something else useful

        // these lines are used for angular to send ws event to frontend, please, implement in your way
//        try {
//            String orderJson = objectMapper.writeValueAsString(order);
//            Message message = MessageBuilder
//                    .withBody(orderJson.getBytes())
//                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
//                    .build();
//            this.messagingTemplate.convertAndSend("/topic/rabbit", message);
//        } catch (JsonProcessingException e) {
//            logger.error("Failed to redirect to rabbit topic", e);
//        }
        logger.debug("Order Received: " + order);
    }

    // uncomment for testing as this order will be sent from this application
//    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
//    public void processOrder2(InputCreateOrderDto order) {
//        logger.debug("Order Received: " + order);
//    }
} 