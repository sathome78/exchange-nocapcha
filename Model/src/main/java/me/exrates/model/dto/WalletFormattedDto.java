package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.Wallet;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by OLEG on 23.03.2017.
 */
@Getter @Setter
@ToString
public class WalletFormattedDto {
  private Integer id;
  private String name;
  private BigDecimal activeBalance;
  private BigDecimal reservedBalance;
  private String activeBalanceFormatted;
  private String reservedBalanceFormatted;
  
  public WalletFormattedDto(Wallet wallet) {
    this.id = wallet.getId();
    this.name = wallet.getName();
    this.activeBalance = wallet.getActiveBalance();
    this.reservedBalance = wallet.getReservedBalance();
    this.activeBalanceFormatted = BigDecimalProcessing.formatNonePoint(this.activeBalance, false);
    this.reservedBalanceFormatted = BigDecimalProcessing.formatNonePoint(this.reservedBalance, false);
  }
}
