package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;

/**
 * Created by OLEG on 06.04.2017.
 */
@Getter @Setter
@ToString
public class CurrencyPairLimitDto {
  private Integer currencyPairId;
  private String currencyPairName;
  private UserRole userRole;
  private OrderType orderType;
  private BigDecimal minRate;
  private BigDecimal maxRate;
}
