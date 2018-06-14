package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2
@Service
public class AchainServiceImpl implements AchainService {

    @Autowired
    private NodeService nodeService;

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        /*autowithdraw not implemented*/
        throw new RuntimeException("autowithdraw not supported");
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = nodeService.getNewAddress();
        return new HashMap<String, String>() {{
            put("address",  address);
           /* put("message", message);*/
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }
}
