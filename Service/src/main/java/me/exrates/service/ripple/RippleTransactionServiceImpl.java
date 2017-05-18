package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/ripple.properties")
public class RippleTransactionServiceImpl implements RippleTransactionService {

    @Autowired
    private RippledNodeService rippledNodeService;
    @Autowired
    private TransactionService transactionService;


    private @Value("${ripple.account.address}") String address;
    private @Value("${ripple.account.secret}") String secret;

    private static final Integer XRP_AMOUNT_MULTIPLIER = 1000000;
    private static final Integer XRP_DECIMALS = 6;
    private static final BigDecimal XRP_MIN_BALANCE = new BigDecimal(20);



    @Override
    public String withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        RippleAccount account = RippleAccount.builder().name(address).secret(secret).build();
        BigDecimal accountFromBalance = getAccountBalance(address);
        log.debug("main acc balance {}", accountFromBalance);
        if (accountFromBalance.compareTo(XRP_MIN_BALANCE) < 0) {
            throw new InsufficientCostsInWalletException();
        }
        RippleTransaction transaction = this.sendMoney(account, new BigDecimal(withdrawMerchantOperationDto.getAmount()),
                withdrawMerchantOperationDto.getAccountTo(), Integer.parseInt(withdrawMerchantOperationDto.getDestinationTag()));
        log.debug("xrp transaction sended {}", transaction);
        return  rippleTransactionToJsonString(transaction);
    }

    /*send xrp*/
    @Transactional
    private RippleTransaction sendMoney(RippleAccount account, BigDecimal amount, String destinationAccount, Integer destinationTag) {
        RippleTransaction transaction = prepareTransaction(amount, account, destinationAccount, destinationTag);
        log.debug("prepared transaction {}", transaction);
        rippledNodeService.signTransaction(transaction);
        rippledNodeService.submitTransaction(transaction);
        return transaction;
    }



    /*https://ripple.com/build/reliable-transaction-submission/*/
    private RippleTransaction prepareTransaction(BigDecimal amount, RippleAccount account, String destinationAccount, Integer destinationTag) {
        Integer lastValidatedLedger = rippledNodeService.getServerState()
                .getJSONObject("state")
                .getJSONObject("validated_ledger")
                .getInt("seq") + 4;
        Integer sequence = rippledNodeService.getAccountInfo(address)
                .getJSONObject("account_data")
                .getInt("Sequence");
        return RippleTransaction.builder()
                .amount(amount)
                .sendAmount(normalizeAmountToXRPString(amount))
                .destinationAddress(destinationAccount)
                .issuerAddress(account.getName())
                .issuerSecret(account.getSecret())
                .destinationTag(destinationTag)
                .lastValidatedLedger(lastValidatedLedger)
                .sequence(sequence)
                .build();
    }

    private String normalizeAmountToXRPString(BigDecimal amount) {
        return amount
                .setScale(XRP_DECIMALS, RoundingMode.HALF_DOWN)
                .multiply(new BigDecimal(XRP_AMOUNT_MULTIPLIER))
                .toBigInteger()
                .toString();
    }

    @Override
    public BigDecimal normalizeAmountToDecimal(String amount) {
        return new BigDecimal(amount)
                .divide(new BigDecimal(XRP_AMOUNT_MULTIPLIER))
                .setScale(XRP_DECIMALS, RoundingMode.HALF_DOWN);
    }


    @Override
    public BigDecimal getAccountBalance(String accountName) {
        JSONObject accountData = rippledNodeService.getAccountInfo(accountName)
                .getJSONObject("account_data");
        return normalizeAmountToDecimal(accountData.getString("Balance"));
    }

    @Override
    public boolean checkSendedTransactionConsensus(String txHash) {
        JSONObject responseBody = new JSONObject(rippledNodeService.getTransaction(txHash));
        String result = responseBody.getJSONObject("meta").getString("TransactionResult");
        return result.equals("tesSUCCESS") && responseBody.getBoolean("validated");
    }

    private String rippleTransactionToJsonString(RippleTransaction rippleTransaction) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hash", rippleTransaction.getTxHash());
        jsonObject.put("sequence", rippleTransaction.getSequence());
        jsonObject.put("ledger", rippleTransaction.getLastValidatedLedger());
        log.debug("transaction {}", jsonObject);
        return jsonObject.toString();
    }




}
