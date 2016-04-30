package me.exrates.controller.validator;

import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final BigDecimal MAX_ORDER_VALUE = new BigDecimal(10000);
    private final BigDecimal MIN_ORDER_VALUE = new BigDecimal(0.000000001);

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ExOrder.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        OrderCreateDto orderCreateDto = (OrderCreateDto) o;
        ValidationUtils.rejectIfEmpty(errors, "amount", "order.fillfield");
        ValidationUtils.rejectIfEmpty(errors, "exchangeRate", "order.fillfield");
        if (orderCreateDto.getAmount() != null) {
            if (orderCreateDto.getAmount().compareTo(MAX_ORDER_VALUE) == 1) {
                errors.rejectValue("amount", "order.maxvalue");
                errors.rejectValue("amount", "order.valuerange");
            }
            if (orderCreateDto.getAmount().compareTo(MIN_ORDER_VALUE) == -1) {
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
            boolean ifEnoughMoney = false;
            int outWalletId = (orderCreateDto.getOperationType() == OperationType.BUY) ? orderCreateDto.getWalletIdCurrencyConvert() : orderCreateDto.getWalletIdCurrencyBase();
            if (outWalletId != 0) {
                if (orderCreateDto.getOperationType() == OperationType.BUY) {
                    ifEnoughMoney = walletService.ifEnoughMoney(outWalletId, orderCreateDto.calculateAmounts().getTotalWithComission());
                } else {
                    ifEnoughMoney = walletService.ifEnoughMoney(outWalletId, orderCreateDto.getAmount());
                }
            }
            if (!ifEnoughMoney) {
                errors.rejectValue("amount", "validation.orderNotEnoughMoney");
            }
        }
    }
}
