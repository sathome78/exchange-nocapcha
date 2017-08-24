package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.handler.OrdersHandler;
import me.exrates.model.ExOrder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Maks on 16.08.2017.
 */
@Log4j2
@Component
@Aspect
public class MethodsExecutionEventAspect {

   /* @Autowired
    private OrdersHandler ordersHandler;

    @After(" execution(* me.exrates.service.impl.OrderServiceImpl.createOrder(..))"
        )
    public void refreshAfterOrderModifById(JoinPoint joinPoint) {
        log.debug("orderModif");
        Object[] signatureArgs = joinPoint.getArgs();
        for (Object signatureArg: signatureArgs) {
            log.debug(signatureArg.toString());
        }
    }

    @After("execution(* me.exrates.dao.impl.OrderDaoImpl.createOrder(..))" +
            "|| execution(* me.exrates.dao.impl.OrderDaoImpl.updateOrder(..)))"
    )
    public void refreshAfterOrderModifByExOrder(JoinPoint joinPoint) {
        log.debug("orderCrOrDel");
        ExOrder exOrder = null;
        for (Object signatureArg: joinPoint.getArgs()) {
            if (signatureArg instanceof ExOrder) {
                log.debug(signatureArg.toString());
                exOrder = (ExOrder)signatureArg;
            }
        }
        if (exOrder != null) {
            ordersHandler.refreshPair(exOrder.getOperationType(), exOrder.getCurrencyPair());
        }
    }*/

}
