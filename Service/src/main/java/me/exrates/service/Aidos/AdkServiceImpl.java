package me.exrates.service.Aidos;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.btcCore.btcDaemon.BtcDaemon;
import me.exrates.service.btcCore.btcDaemon.BtcHttpDaemonImpl;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Log4j2
@Service
public class AdkServiceImpl implements AdkService {

    private final AidosNodeService aidosNodeService;
    private final MessageSource messageSource;

    @Autowired
    public AdkServiceImpl(AidosNodeService aidosNodeService, MessageSource messageSource) {
        this.aidosNodeService = aidosNodeService;
        this.messageSource = messageSource;
    }


    private BtcdClient btcdClient;

    private Boolean supportInstantSend;
    private Boolean supportSubtractFee;
    private Boolean supportReferenceLine;

    private BtcDaemon btcDaemon;

    @PostConstruct
    private void init() {

        initCoreClient("node_config/node_config_adk.properties", false, false, false);
        initBtcdDaemon();
    }

    public void initCoreClient(String nodePropertySource, boolean supportInstantSend, boolean supportSubtractFee, boolean supportReferenceLine) {
        try {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
                    .build();
            Properties nodeConfig = new Properties();
            nodeConfig.load(getClass().getClassLoader().getResourceAsStream(nodePropertySource));
            log.info("Node config: " + nodeConfig);
            btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
            this.supportInstantSend = supportInstantSend;
            this.supportSubtractFee = supportSubtractFee;
            this.supportReferenceLine = supportReferenceLine;
        } catch (Exception e) {
            log.error("Could not initialize BTCD client of config {}. Reason: {} ", nodePropertySource, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

    }

    public void initBtcdDaemon()  {
            btcDaemon = new BtcHttpDaemonImpl(btcdClient);
        try {
            btcDaemon.init();
        } catch (Exception e) {
            log.error(e);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = aidosNodeService.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }


    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented");
    }
}
