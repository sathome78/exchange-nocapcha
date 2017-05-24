package me.exrates.model.dto.btcTransactionFacade;

import lombok.*;

import java.math.BigDecimal;

/**
 * Created by OLEG on 18.05.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BtcTxPaymentDto {
  
  private String address;
  private String category;
  private BigDecimal amount;
  private BigDecimal fee;
}
