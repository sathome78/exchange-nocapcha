//package me.exrates.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.log4j.Log4j2;
//import me.exrates.model.dto.InputCreateOrderDto;
//import me.exrates.model.dto.OrderCreateSummaryDto;
//import me.exrates.model.enums.OperationType;
//import me.exrates.model.enums.OrderBaseType;
//import me.exrates.service.exception.RabbitMqException;
//import me.exrates.service.handler.OrdersEventHandleService;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageBuilder;
//import org.springframework.amqp.core.MessageProperties;
//import org.springframework.amqp.rabbit.annotation.EnableRabbit;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//import static me.exrates.model.enums.OrderBaseType.convert;
//
//@Log4j2
//@EnableRabbit
//@Component
//public class OrderMessageJspListener {
//
//
//    @Autowired
//    private OrdersEventHandleService ordersEventHandleService;
//
//    @Autowired
//    private OrderServiceDemoListener orderServiceDemoListener;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    /*
//     * This method is triggered if rabbit exchange receives message ({order}) from counterpart application
//     */
////    @RabbitListener(queues = RabbitMqService.JSP_QUEUE)
////    public Message processOrder(Message message) {
////        String processId = (String) message.getMessageProperties().getHeaders().get("process-id");
////        log.info("{} ProcessId from demo Received:", processId);
////        InputCreateOrderDto order;
////        try {
////            order = objectMapper.readValue(message.getBody(), InputCreateOrderDto.class);
////            log.info("{} Received order from demo {}", processId, order);
////        } catch (IOException e) {
////            log.error("{} Failed read orderJson, e {}", processId, e);
////            throw new RabbitMqException("Failed read orderJson e {}" + e.getLocalizedMessage());
////        }
////        OperationType orderType = OperationType.valueOf(order.getOrderType());
////        OrderBaseType baseType = convert(order.getBaseType());
////
////        OrderCreateSummaryDto orderCreateSummaryDto = orderServiceDemoListener.newOrderToSell(orderType, order.getUserId(),
////                order.getAmount(), order.getRate(), baseType, order.getCurrencyPairId(), order.getStop());
////
////        log.info("{} Creating OrderCreateSummaryDto {}", processId, orderCreateSummaryDto);
////
////        String orderToDB = orderServiceDemoListener.recordOrderToDB(order, orderCreateSummaryDto.getOrderCreateDto());
////        log.info("{} Recorder to DB {}", processId, orderToDB);
////
////        ordersEventHandleService.handleOrderEventOnMessage(order);
////        log.info("{} Order saved: {}", processId, order);
////
////        String success = "success";
////        Message messageBack = MessageBuilder
////                .withBody(success.getBytes())
////                .setHeader("process-id", processId)
////                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
////                .build();
////
////        return messageBack;
////    }
//
//    // uncomment for testing as this order will be sent from this application
////    @RabbitListener(queues = RabbitMqService.ANGULAR_QUEUE)
////    public void processOrderSelf(InputCreateOrderDto order) {
////        logger.debug("Order Received: " + order);
////    }
//}