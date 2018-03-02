package me.exrates.service.nem;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.dto.MosaicIdDto;
import me.exrates.model.dto.MosaicIdDto;

import java.math.BigDecimal;

/**
 * Created by Maks on 27.02.2018.
 */
public class XemMosaicServiceImpl implements XemMosaicService {

    private String merchantName;
    private String currencyName;
    private MosaicIdDto mosaicIdDto;
    private long decimals;
    private BigDecimal nemExRate;

    public XemMosaicServiceImpl(String merchantName, String currencyName, MosaicIdDto mosaicIdDto, long decimals, BigDecimal nemExRate) {
        this.merchantName = merchantName;
        this.currencyName = currencyName;
        this.mosaicIdDto = mosaicIdDto;
        this.decimals = decimals;
        this.nemExRate = nemExRate;
    }

    public BigDecimal getNemExRate() {
        return nemExRate;
    }

    @Override
    public MosaicIdDto getMosaicId() {
        return mosaicIdDto;
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
