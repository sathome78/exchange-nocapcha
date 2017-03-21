package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.MerchantImage;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class WithdrawRequestFlatAdditionalDataDto {
  private String userEmail;
  private String currencyName;
  private String merchantName;
  private String adminHolderEmail;
  private MerchantImage merchantImage;
}
