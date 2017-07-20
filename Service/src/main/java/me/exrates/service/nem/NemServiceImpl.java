package me.exrates.service.nem;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.nem.core.crypto.KeyPair;
import org.nem.core.crypto.PrivateKey;
import org.nem.core.model.Account;
import org.nem.core.model.Transaction;
import org.nem.core.model.TransferTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.stellar.sdk.responses.TransactionResponse;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

/**
 * Created by maks on 18.07.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemServiceImpl implements NemService {

    @Autowired
    private NemTransactionsService nemTransactionsService;

    private @Value("${ncc.server.url}")String nccServer;
    private @Value("${nis.server.url}")String nisServer;
    private @Value("${nem.address}")String address;
    private @Value("${nem.private.key}")String privateKey;
    private @Value("${nem.public.key}")String publicKey;

    protected Account account;

    @Override
    public Account getAccount() {
        return account;
    }

    @PostConstruct
    public void init() {
        account = new Account(new KeyPair(PrivateKey.fromHexString(privateKey)));
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public void onTransactionReceive(TransactionResponse payment, String amount) {

    }
}
