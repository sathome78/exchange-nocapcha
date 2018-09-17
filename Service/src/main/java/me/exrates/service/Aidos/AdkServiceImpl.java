package me.exrates.service.Aidos;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdkServiceImpl implements AdkService {

    private final AidosNodeService aidosNodeService;
    private final MessageSource messageSource;

    @Autowired
    public AdkServiceImpl(AidosNodeService aidosNodeService, MessageSource messageSource) {
        this.aidosNodeService = aidosNodeService;
        this.messageSource = messageSource;
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
