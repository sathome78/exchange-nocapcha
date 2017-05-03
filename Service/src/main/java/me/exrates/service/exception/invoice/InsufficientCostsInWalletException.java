package me.exrates.service.exception.invoice;

/**
 * Created by OLEG on 28.04.2017.
 */
public class InsufficientCostsInWalletException extends MerchantException {
  public InsufficientCostsInWalletException() {
  }
  
  public InsufficientCostsInWalletException(String message) {
    super(message);
  }
  
  public InsufficientCostsInWalletException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public InsufficientCostsInWalletException(Throwable cause) {
    super(cause);
  }
}
