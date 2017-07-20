package me.exrates.service.nem;

import com.google.gson.JsonDeserializationContext;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import org.json.JSONObject;
import org.nem.core.crypto.KeyPair;
import org.nem.core.crypto.Signer;
import org.nem.core.messages.PlainMessage;
import org.nem.core.model.Account;
import org.nem.core.model.Address;
import org.nem.core.model.TransferTransaction;
import org.nem.core.model.TransferTransactionAttachment;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.DeserializationContext;
import org.nem.core.serialization.JsonDeserializer;
import org.nem.core.serialization.JsonSerializer;
import org.nem.core.serialization.SimpleAccountLookup;
import org.nem.core.time.TimeInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Created by maks on 18.07.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemTransactionsService {

    private @Value("${ncc.server.url}")String nccServer;
    private @Value("${nis.server.url}")String nisServer;

    int version_main = 1744830465;
    int version_test = -1744830463;


    @Autowired
    private NemService nemService;


    public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        JSONObject jsonObject = new JSONObject();
        SimpleAccountLookup lookup = new SimpleAccountLookup() {
            @Override
            public Account findByAddress(Address address) {
                return nemService.getAccount();
            }
        };
        DeserializationContext context = new DeserializationContext(lookup);
        JsonDeserializer deserializer = new JsonDeserializer(new net.minidev.json.JSONObject(), context);
        Account reipient = new Account(Address.fromEncoded(withdrawMerchantOperationDto.getAccountTo()));
        TransferTransactionAttachment attachment = new TransferTransactionAttachment(new PlainMessage(withdrawMerchantOperationDto.getDestinationTag().getBytes()));
        TransferTransaction transaction = new  TransferTransaction(version_test, TimeInstant.ZERO,
                nemService.getAccount(), reipient, transformToNemAmount(withdrawMerchantOperationDto.getAmount()),  attachment);
        transaction.sign();

        JsonSerializer serializer = new JsonSerializer();
        transaction.serialize(serializer);
        serializer.getObject().toJSONString();

    }

    private Amount transformToNemAmount(String amount) {
        return Amount.ZERO;/*todo*/
    }
}
