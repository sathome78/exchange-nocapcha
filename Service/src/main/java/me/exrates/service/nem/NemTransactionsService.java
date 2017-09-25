package me.exrates.service.nem;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.*;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.NemTransactionException;
import me.exrates.service.exception.WithdrawRequestPostException;
import org.json.JSONObject;
import org.nem.core.crypto.PrivateKey;
import org.nem.core.messages.PlainMessage;
import org.nem.core.model.*;
import org.nem.core.model.Transaction;
import org.nem.core.model.ncc.RequestPrepareAnnounce;
import org.nem.core.model.primitive.Amount;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.serialization.DeserializationContext;
import org.nem.core.serialization.JsonSerializer;
import org.nem.core.serialization.SimpleAccountLookup;
import org.nem.core.time.TimeInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * Created by maks on 18.07.2017.
 */
@Log4j2(topic = "nem_log")
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemTransactionsService {

    private @Value("${nem.transaction.version}")Integer version;


    private static final int decimals = 6;

    @Autowired
    private NemService nemService;
    @Autowired
    private NemNodeService nodeService;

    private TransactionFeeCalculatorAfterFork calculatorAfterFork = new TransactionFeeCalculatorAfterFork();

    @PostConstruct
    public void init() {
        switch (version) {
            case 1 :{
                NetworkInfos.setDefault(NetworkInfos.getMainNetworkInfo());
                break;
            }
            default: {
                NetworkInfos.setDefault(NetworkInfos.getTestNetworkInfo());
                break;
            }
        }

    }

    public HashMap<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto, String privateKey) {
        try {
            TransferTransaction transaction = prepareTransaction(withdrawMerchantOperationDto);
            JsonSerializer serializer = new JsonSerializer();
            RequestPrepareAnnounce announce = new RequestPrepareAnnounce(transaction, PrivateKey.fromHexString(privateKey));
            announce.serialize(serializer);
            JSONObject result = nodeService.anounceTransaction(serializer.getObject().toJSONString());
            return new HashMap<String, String>() {{
                put("hash", result.getJSONObject("transactionHash").getString("data"));
            }};
        } catch (Exception e) {
            log.error(e);
            throw new WithdrawRequestPostException("error post NEM withdraw");
        }
    }

    private TransferTransaction prepareTransaction(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        Account reipient = new Account(Address.fromEncoded(withdrawMerchantOperationDto.getAccountTo().replaceAll("-", "").trim()));
        TimeInstant currentTimeStamp = nodeService.getCurrentTimeStamp();
        TransferTransactionAttachment attachment = null;
        try {
            attachment = new TransferTransactionAttachment(new PlainMessage(withdrawMerchantOperationDto.getDestinationTag().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            log.error("unsupported encoding {}", e);
        }
        TransferTransaction transaction = new  TransferTransaction(currentTimeStamp,
                nemService.getAccount(), reipient, transformToNemAmount(withdrawMerchantOperationDto.getAmount()),  attachment);
        transaction.setDeadline(currentTimeStamp.addHours(2));
        transaction.setFee(calculatorAfterFork.calculateMinimumFee(transaction));
        return transaction;
    }

    private Amount transformToNemAmount(String amount) {
        BigDecimal a = new BigDecimal(amount).setScale(decimals, RoundingMode.HALF_DOWN).multiply(new BigDecimal(1000000));
        return new Amount(a.longValue());
    }

    String transformToString(long nemAmount) {
        BigDecimal a = new BigDecimal(nemAmount).setScale(decimals, RoundingMode.HALF_DOWN).divide(new BigDecimal(1000000));
        return a.toPlainString();
    }

    boolean checkIsConfirmed(JSONObject transactionMetaDataPair, int confirmationsNeeded) {
        JSONObject responseMeta = transactionMetaDataPair.getJSONObject("meta");
        log.debug("xem check confirmations {}", responseMeta);
        if (responseMeta.has("height")) {
            long height = responseMeta.getLong("height");
            long currentHeight = nodeService.getLastBlockHeight();
            log.debug("currentHeight {}", currentHeight);
            return (currentHeight - height) >= confirmationsNeeded;
        }
        return false;
    }


    void checkForOutdate(JSONObject transaction) {
        TimeInstant current = nodeService.getCurrentTimeStamp();
        TimeInstant deadline = new TimeInstant(transaction.getJSONObject("transaction").getInt("deadline"));
        if (current.compareTo(deadline) <= 0) {
            throw new NemTransactionException("NEM transaction was not included into block");
        }
    }

    BigDecimal countTxFee(BigDecimal amount, String destinationTag) {
        Transaction transaction = prepareTransaction(WithdrawMerchantOperationDto.builder()
                        .accountTo("")
                .amount(amount.toPlainString())
                .destinationTag(destinationTag)
                .build());

        return new BigDecimal(transformToString(transaction.getFee().getNumMicroNem()));
    }
}
