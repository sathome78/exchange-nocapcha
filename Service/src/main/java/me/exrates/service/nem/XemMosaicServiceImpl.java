package me.exrates.service.nem;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Maks on 27.02.2018.
 */
public class XemMosaicServiceImpl implements XemMosaicService {

    private String merchantName;
    private String currencyName;
    private String mosaicName;
    private long decimals;

    public XemMosaicServiceImpl(String merchantName, String currencyName, String mosaicName, long decimals) {
        this.merchantName = merchantName;
        this.currencyName = currencyName;
        this.mosaicName = mosaicName;
        this.decimals = decimals;
    }


    @Override
    public String getMosaicName() {
        return mosaicName;
    }

    @Override
    public String getMerchantName() {
        return merchantName;
    }

    @Override
    public String getCurrencyName() {
        return currencyName;
    }

    @Override
    public long getDecimals() {
        return decimals;
    }
}
