package me.exrates.model.dto.mobileApiDto.dashboard;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;

import java.util.List;

/**
 * Created by OLEG on 29.06.2017.
 */
@Getter @Setter
@ToString
public class GeneralInfoDto {
  List<CurrencyPairWithLimitsDto> currencyPairs;
  List<MerchantCurrencyApiDto> merchants;
  CommissionsDto commissions;
  List<TransferLimitDto> transferLimits;
}
