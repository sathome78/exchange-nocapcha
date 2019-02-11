package me.exrates.service.ppy;

import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.bitshares.BitsharesService;
import me.exrates.service.bitshares.BitsharesServiceImpl;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.exrates.service.bitshares.MemoDecryptor.decryptBTSmemo;


@ClientEndpoint
public class PPYServiceImpl extends BitsharesServiceImpl {

    public PPYServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY) {
        super(merchantName, currencyName, propertySource, SCANING_INITIAL_DELAY);
    }

    @OnMessage
    @Override
    public void onMessage(String msg) {
        log.info(msg);
        try {
            if (msg.contains("last_irreversible_block_num")) setIrreversableBlock(msg);
            else if (msg.contains("previous")) processIrreversebleBlock(msg);
            else log.info("unrecogrinzed msg from " + merchantName + "\n" + msg);
        } catch (Exception e) {
            log.error("Web socket error" + merchantName + "  : \n" + e.getMessage());
        }

    }

    @Override
    protected void setIrreversableBlock(String msg) {
        JSONObject message = new JSONObject(msg);
        int blockNumber = message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(3).getInt(lastIrreversebleBlockParam);
        synchronized (this) {
            if (blockNumber > lastIrreversibleBlockValue) {
                for (; lastIrreversibleBlockValue <= blockNumber; lastIrreversibleBlockValue++) {
                    getBlock(lastIrreversibleBlockValue);
                }
                merchantSpecParamsDao.updateParam(merchant.getName(), lastIrreversebleBlockParam, String.valueOf(lastIrreversibleBlockValue));
            }
        }
    }
}
