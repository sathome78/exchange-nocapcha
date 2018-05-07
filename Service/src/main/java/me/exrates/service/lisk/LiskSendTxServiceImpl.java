package me.exrates.service.lisk;

import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import org.springframework.beans.factory.annotation.Autowired;


public class LiskSendTxServiceImpl implements LiskSendTxService {

    @Autowired
    private LiskRestClient liskRestClient;

    @Override
    public String sendTransaction(String secret, Long amount, String recipientId) {
        LiskSendTxDto dto = new LiskSendTxDto();
        dto.setSecret(secret);
        dto.setAmount(amount);
        dto.setRecipientId(recipientId);
        return liskRestClient.sendTransaction(dto);
    }
}
