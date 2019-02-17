package me.exrates.model.dto.onlineTableDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.CurrencyPairType;

/**
 * Created by Valk
 */
@Getter @Setter @ToString
public class ExOrderStatisticsShortByPairsDto extends OnlineTableDto {

  private Integer currencyPairId;
  private String currencyPairName;
  private Integer currencyPairScale;
  private String lastOrderRate;
  private String predLastOrderRate;
  private String percentChange;
  private String market;
  private String volume;
  private String priceInUSD;
  private CurrencyPairType type;
  @JsonIgnore
  private Integer pairOrder;
  @JsonIgnore
  private Integer currency1Id;
  private String currencyVolume;

  public ExOrderStatisticsShortByPairsDto() {
    this.needRefresh = true;
  }

  public ExOrderStatisticsShortByPairsDto(boolean needRefresh) {
    this.needRefresh = needRefresh;
  }

  public ExOrderStatisticsShortByPairsDto(ExOrderStatisticsShortByPairsDto exOrderStatisticsShortByPairsDto) {
    this.needRefresh = exOrderStatisticsShortByPairsDto.isNeedRefresh();
    this.page = exOrderStatisticsShortByPairsDto.getPage();
    this.currencyPairName = exOrderStatisticsShortByPairsDto.getCurrencyPairName();
    this.currencyPairScale = exOrderStatisticsShortByPairsDto.getCurrencyPairScale();
    this.lastOrderRate = exOrderStatisticsShortByPairsDto.getLastOrderRate();
    this.predLastOrderRate = exOrderStatisticsShortByPairsDto.getPredLastOrderRate();
    this.percentChange = exOrderStatisticsShortByPairsDto.getPercentChange();
    this.type = exOrderStatisticsShortByPairsDto.getType();
    this.currencyPairId = exOrderStatisticsShortByPairsDto.getCurrencyPairId();
    this.pairOrder = exOrderStatisticsShortByPairsDto.getPairOrder();
  }

  @Override
  public int hashCode() {
    int result = currencyPairName != null ? currencyPairName.hashCode() : 0;
    result = 31 * result + (lastOrderRate != null ? lastOrderRate.hashCode() : 0);
    result = 31 * result + (predLastOrderRate != null ? predLastOrderRate.hashCode() : 0);
    return result;
  }

}