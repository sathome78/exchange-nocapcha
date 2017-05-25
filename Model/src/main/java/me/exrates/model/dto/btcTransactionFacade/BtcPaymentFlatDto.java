package me.exrates.model.dto.btcTransactionFacade;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.core.util.Assert;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by OLEG on 25.05.2017.
 */
@Getter @Setter
@Builder
@ToString
public class BtcPaymentFlatDto {
  private BigDecimal amount;
  private Integer confirmations;
  private String txId;
  private String address;
  private Integer merchantId;
  private Integer currencyId;
  
  public static BtcPaymentFlatDto resolveFromParams(Map<String, String> params, Integer merchantId, Integer currencyId) {
    return builder()
            .amount(new BigDecimal(getIfNotNull(params, "amount")))
            .confirmations(Integer.parseInt(getIfNotNull(params, "confirmations")))
            .txId(getIfNotNull(params, "txId"))
            .address(getIfNotNull(params, "address"))
            .merchantId(merchantId)
            .currencyId(currencyId).build();
  }
  
  private static String getIfNotNull(Map<String, String> params, String paramName) {
    String value = params.get(paramName);
    Assert.requireNonNull(value, String.format("Absent value for param %s", paramName));
    return value;
  }
  
}
