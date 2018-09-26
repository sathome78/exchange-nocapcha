package me.exrates.service.aidos;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.btc.BtcPaymentFlatDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.CoreWalletPasswordNotFoundException;
import me.exrates.service.exception.IncorrectCoreWalletPasswordException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2(topic = "adk_log")
@Service
public class AdkServiceImpl implements AdkService {

    private final AidosNodeService aidosNodeService;
    private final MessageSource messageSource;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final RefillService refillService;

    private static final String CURRENCY_NAME = "ADK";
    private static final String MERCHANT_NAME = "ADK";
    private Merchant merchant;
    private Currency currency;
    private static final Integer SECONDDS_TO_UNLOCK_WALLET = 60;

    @Autowired
    public AdkServiceImpl(AidosNodeService aidosNodeService, MessageSource messageSource, MerchantService merchantService, CurrencyService currencyService, RefillService refillService) {
        this.aidosNodeService = aidosNodeService;
        this.messageSource = messageSource;
        this.merchantService = merchantService;
        this.currencyService = currencyService;
        this.refillService = refillService;
    }

    @PostConstruct
    private void inti() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    @Override
    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public MerchantService getMerchantService() {
        return merchantService;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(CURRENCY_NAME);
        Merchant merchant = merchantService.findByName(MERCHANT_NAME);
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
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
    public RefillRequestAcceptDto createRequest(BtcTransactionDto transactionDto) {
        if (isTransactionDuplicate(transactionDto.getTxId(), currency.getId(), merchant.getId())) {
            log.error("ADK transaction allready received!!! {}", transactionDto);
            throw new RuntimeException("ADK transaction allready received!!!");
        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(transactionDto.getDetails().get(0).getAddress())
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(transactionDto.getDetails().get(0).getAmount())
                .merchantTransactionId(transactionDto.getTxId())
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        return requestAcceptDto;
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {
        try {
            refillService.putOnBchExamRefillRequest(
                    RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestAcceptDto.getRequestId())
                            .merchantId(merchant.getId())
                            .currencyId(currency.getId())
                            .address(requestAcceptDto.getAddress())
                            .amount(requestAcceptDto.getAmount())
                            .hash(requestAcceptDto.getMerchantTransactionId())
                            .build());
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getBalance() {
        return aidosNodeService.getBalance().toPlainString();
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        JSONArray array = aidosNodeService.getAllTransactions();
        return StreamSupport.stream(array.spliterator(), false)
                .map(transaction -> dtoMapper((JSONObject) transaction)).collect(Collectors.toList());
    }

    @Override
    public void unlockWallet(String password) {
        String storedPassword =  merchantService.getCoreWalletPassword(merchant.getName(), currency.getName())
                .orElseThrow(() -> new CoreWalletPasswordNotFoundException(String.format("pass not found for merchant %s currency %s", merchant.getName(), currency.getName())));
        if (password == null || !password.equals(storedPassword)) {
            throw new IncorrectCoreWalletPasswordException("Incorrect password: " + password);
        }
        Preconditions.checkState(aidosNodeService.unlockWallet(password, SECONDDS_TO_UNLOCK_WALLET), "Wallet unlocking error");
    }

    private BtcTransactionHistoryDto dtoMapper(JSONObject jsonObject) {
        BtcTransactionHistoryDto dto = new BtcTransactionHistoryDto();
        dto.setAddress(jsonObject.getString("address"));
        dto.setAmount(jsonObject.getString("amount"));
        dto.setCategory(jsonObject.getString("category"));
        dto.setConfirmations(jsonObject.getInt("confirmations"));
        dto.setTxId("txid");
        dto.setTime(new Timestamp(jsonObject.getInt("time")).toLocalDateTime());

        return dto;
    }

    public static String getCurrencyName() {
        return CURRENCY_NAME;
    }

    public static String getMerchantName() {
        return MERCHANT_NAME;
    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }
}
