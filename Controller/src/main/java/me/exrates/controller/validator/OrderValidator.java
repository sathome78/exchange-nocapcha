package me.exrates.controller.validator;

import me.exrates.model.Order;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import java.math.BigDecimal;

/**
 * Created by Valk on 31.03.16.
 */
@Component
public class OrderValidator implements Validator {
    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Order.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        OrderCreateDto orderCreateDto = (OrderCreateDto) o;
        ValidationUtils.rejectIfEmpty(errors, "amount", "order.fillfield");
        ValidationUtils.rejectIfEmpty(errors, "exchangeRate", "order.fillfield");
        if (orderCreateDto.getAmount() != null) {
            if (orderCreateDto.getAmount().compareTo(new BigDecimal(10000)) == 1) {
                errors.rejectValue("amount", "order.maxvalue");
                errors.rejectValue("amount", "order.valuerange");
            }
            if (orderCreateDto.getAmount().compareTo(new BigDecimal(0.000000001)) == -1) {
                errors.rejectValue("amount", "order.minvalue");
                errors.rejectValue("amount", "order.valuerange");
            }
        }
        if (orderCreateDto.getExchangeRate() != null) {
            if (orderCreateDto.getExchangeRate().compareTo(new BigDecimal(0)) < 1) {
                errors.rejectValue("exchangeRate", "order.minrate");
            }
        }

        //check for enoughMoney
        if ((orderCreateDto.getAmount() != null) && (orderCreateDto.getExchangeRate() != null)) {
            int walletId;
            if (orderCreateDto.getOperationType() == OperationType.BUY) {
                walletId = orderCreateDto.getWalletIdCurrency2();
            } else {
                walletId = orderCreateDto.getWalletIdCurrency1();
            }
            boolean ifEnoughMoney = false;
            if (walletId != 0) {
                ifEnoughMoney = walletService.ifEnoughMoney(walletId, getCalculatedSum(orderCreateDto).totalWithComission);
            }
            if (!ifEnoughMoney) {
                errors.rejectValue("amount", "validation.orderNotEnoughMoney");
            }
        }
    }

    public OrderSum getCalculatedSum(OrderCreateDto orderCreateDto){
        OrderSum result = new OrderSum();
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            result.total = orderCreateDto.getAmount().multiply(orderCreateDto.getExchangeRate());
            result.comission = result.total.multiply(orderCreateDto.getComissionForBuy()).divide(new BigDecimal(100));
            result.totalWithComission = result.total.add(result.comission);
        } else {
            result.total = orderCreateDto.getAmount().multiply(orderCreateDto.getExchangeRate());
            result.comission = result.total.multiply(orderCreateDto.getComissionForSell()).divide(new BigDecimal(100));
            result.totalWithComission = result.total.add(result.comission.negate());
        }
        return result;
    }

    public class OrderSum{
        public BigDecimal total;
        public BigDecimal comission;
        public BigDecimal totalWithComission;
    }

}
