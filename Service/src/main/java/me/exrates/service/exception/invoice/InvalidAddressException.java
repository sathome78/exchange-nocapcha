package me.exrates.service.exception.invoice;

/**
 * Created by OLEG on 28.04.2017.
 */
public class InvalidAddressException extends MerchantException {
  public InvalidAddressException() {
  }
  
  public InvalidAddressException(String message) {
    super(message);
  }
  
  public InvalidAddressException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public InvalidAddressException(Throwable cause) {
    super(cause);
  }
}
