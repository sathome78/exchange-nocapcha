package me.exrates.service.nem;

import me.exrates.model.dto.MosaicIdDto;

import java.math.BigDecimal;

/**
 * Created by Maks on 27.02.2018.
 */
public interface XemMosaicService {

    MosaicIdDto getMosaicId();

    String getMerchantName();

    String getCurrencyName();

    long getDecimals();

    BigDecimal getNemExRate();
}
