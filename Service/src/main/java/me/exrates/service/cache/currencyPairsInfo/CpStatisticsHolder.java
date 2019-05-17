package me.exrates.service.cache.currencyPairsInfo;

import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;

public interface CpStatisticsHolder {

    void onOrderAccept(Integer pairId);

    ResponseInfoCurrencyPairDto get(String pairName);

    ResponseInfoCurrencyPairDto get(Integer pairId);
}
