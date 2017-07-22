package me.exrates.service.nem;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.CheckDestinationTagException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.WithdrawRequestPostException;
import org.json.JSONObject;
import org.nem.core.crypto.KeyPair;
import org.nem.core.crypto.PrivateKey;
import org.nem.core.model.Account;
import org.nem.core.model.NemGlobals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by maks on 18.07.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemServiceImpl implements NemService {

    @Autowired
    private NemTransactionsService nemTransactionsService;
    @Autowired
    private NemNodeService nodeService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AlgorithmService algorithmService;

    private static final String NEM_MERCHANT = "NEM";
    private static final int CONFIRMATIONS_COUNT_WITHDRAW = 2; /*must be 20, but in this case its safe for us to check only 2 confirmations*/

    private Merchant merchant;
    private Currency currency;


    @PostConstruct
    public void init() {
        account = new Account(new KeyPair(PrivateKey.fromHexString(privateKey)));
        currency = currencyService.findByName("XEM");
        merchant = merchantService.findByName(NEM_MERCHANT);
    }


    private @Value("${nem.address}")String address;
    private @Value("${nem.private.key}")String privateKey;
    private @Value("${nem.public.key}")String publicKey;

    private static final String DESTINATION_TAG_ERR_MSG = "message.nem.tagError";

    protected Account account;

    @Override
    public Account getAccount() {
        return account;
    }

    @Transactional
    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.debug("withdraw_XEM");
        if (!"XEM".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        return nemTransactionsService.withdraw(withdrawMerchantOperationDto, privateKey);
    }

    @Transactional
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.XEM",
                new Object[]{address, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address",  destinationTag);
            put("message", message);
        }};
    }

    private String generateUniqDestinationTag(int userId) {
        Optional<Integer> id = null;
        String destinationTag;
        int counter = 0;
        do {
            destinationTag = generateDestinationTag(String.valueOf(userId).concat(String.valueOf(counter)));
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(
                    destinationTag,
                    currency.getId(),
                    merchant.getId());
            counter = counter + 8;
        } while (id.isPresent());
        log.debug("tag is {}", destinationTag);
        return destinationTag;
    }

    private String generateDestinationTag(String id) {
       return algorithmService.sha256(String.valueOf(id));
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }


    @Override
    public boolean checkSendedTransaction(String hash, String additionalParams) {
        JSONObject transaction = nodeService.getSingleTransactionByHash(hash);
        if (nemTransactionsService.checkIsConfirmed(transaction, CONFIRMATIONS_COUNT_WITHDRAW)) {
            return true;
        }
        nemTransactionsService.checkForOutdate(transaction);
        return false;
    }

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag) {
        return nemTransactionsService.countTxFee(amount, destinationTag);
    }


    /*message must be not more than 512 bytes*/
    @Override
    public void checkDestinationTag(String destinationTag) {
        try {
            if (destinationTag.getBytes("UTF-8").length > 512) {
                throw new CheckDestinationTagException(DESTINATION_TAG_ERR_MSG, this.additionalWithdrawFieldName());
            }
        } catch (UnsupportedEncodingException e) {
            log.error("unsupported encoding {}", e);
        }
    }

    @Override
    public String getMainAddress() {
        return address;
    }
}
