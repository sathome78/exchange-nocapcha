package me.exrates.controller.validator;

import me.exrates.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

/**
 * Created by Valk on 31.03.16.
 */
@Component
public class OrderValidator implements Validator {
/*    @Autowired
    MessageSource messageSource;*/

    @Override
    public boolean supports(Class<?> aClass) {
        return Order.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Order order = (Order) o;
        ValidationUtils.rejectIfEmpty(errors, "amountSell", "order.fillfield");
        if (order.getAmountSell() != null) {
            if (order.getAmountSell().compareTo(new BigDecimal(10000)) == 1) {
                errors.rejectValue("amountSell", "order.maxvalue");
                errors.rejectValue("amountSell", "order.valuerange");
            }
            if (order.getAmountSell().compareTo(new BigDecimal(0.000000001)) == -1) {
                errors.rejectValue("amouuntSell", "order.minvalue");
                errors.rejectValue("amouuntSell", "order.valuerange");
            }
        }
        /**/
        ValidationUtils.rejectIfEmpty(errors, "amountBuy", "order.fillfield");
        if (order.getAmountBuy() != null) {
            if (order.getAmountBuy().compareTo(new BigDecimal(10000)) == 1) {
                errors.rejectValue("amountBuy", "order.maxvalue");
                errors.rejectValue("amountBuy", "order.valuerange");
            }
            if (order.getAmountBuy().compareTo(new BigDecimal(0.000000001)) == -1) {
                errors.rejectValue("amountBuy", "order.minvalue");
                errors.rejectValue("amountBuy", "order.valuerange");
            }
        }
    }
}
