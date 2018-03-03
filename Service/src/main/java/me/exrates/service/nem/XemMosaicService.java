package me.exrates.service.nem;

import me.exrates.model.dto.MosaicIdDto;
import org.nem.core.model.mosaic.MosaicId;
import org.nem.core.model.primitive.Supply;

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

    MosaicId mosaicId();

    Supply getSupply();

    int getDivisibility();
}
