package me.exrates.service.exception;

/**
 * Created by ValkSam
 */
public class RefillRequestLimitForMerchantExceededException extends RuntimeException{
    public RefillRequestLimitForMerchantExceededException(String message) {
        super(message);
    }
}
