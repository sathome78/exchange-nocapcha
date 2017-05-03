package me.exrates.service.exception.invoice;

/**
 * Created by OLEG on 28.04.2017.
 */
public class MerchantException extends RuntimeException {
  public MerchantException() {
  }
  
  public MerchantException(String message) {
    super(message);
  }
  
  public MerchantException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public MerchantException(Throwable cause) {
    super(cause);
  }
}
