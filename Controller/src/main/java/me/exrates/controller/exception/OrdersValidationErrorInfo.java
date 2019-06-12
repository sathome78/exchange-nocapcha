package me.exrates.controller.exception;

import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.ngExceptions.NgOrderValidationException;


public class OrdersValidationErrorInfo extends ErrorInfo {

    private OrderValidationDto validationResults;

    public OrdersValidationErrorInfo(CharSequence url, NgOrderValidationException ex) {
        super(url, ex);
        validationResults = ex.getValidationResults();
    }
}
