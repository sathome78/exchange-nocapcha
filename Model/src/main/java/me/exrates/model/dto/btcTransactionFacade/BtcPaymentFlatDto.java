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
  private String blockhash;
  private Integer merchantId;
  private Integer currencyId;
}
