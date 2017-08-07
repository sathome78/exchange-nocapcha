package me.exrates.service.impl;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;
import jota.error.*;
import jota.model.Bundle;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.Checksum;
import jota.utils.IotaUnitConverter;
import jota.utils.IotaUnits;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.IotaService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by ajet
 */
@Log4j2(topic = "merchant")
@Service
@PropertySource("classpath:/merchants/iota.properties")
public class IotaServiceImpl implements IotaService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    private IotaAPI iotaClient;

    private @Value("${iota.protocol}")String PROTOCOL;
    private @Value("${iota.host}")String HOST;
    private @Value("${iota.port}")String PORT;
    private @Value("${iota.seed}")String SEED;
    private @Value("${iota.message}")String MESSAGE;
    private @Value("${iota.tag}")String TAG;

    private static List<String> ADDRESSES = new ArrayList<>();

    private Merchant merchant;

    private Currency currency;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        return new HashMap<>();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        BigDecimal amount = BigDecimal.valueOf(IotaUnitConverter.convertAmountTo(Long.parseLong(params.get("amount")), IotaUnits.MEGA_IOTA));

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(params.get("address"))
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(params.get("hash"))
                .build();

        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    @Transactional
    public Map<String, String> refill(RefillRequestCreateDto request) {

        Map<String, String> mapAddress = new HashMap<>();
        String address = "";
        try {
            address = iotaClient.getNewAddress(SEED, 2, 0, true, 0, false).getAddresses().get(0);
            List<Transfer> transfers = new ArrayList<>();
            transfers.add(new jota.model.Transfer(address, 0, MESSAGE, TAG));
            iotaClient.sendTransfer(SEED, 2, 9, 15, transfers, null, null);
            ADDRESSES.add(address);

        } catch (Exception e) {
            log.error(e);
        }

        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());

        mapAddress.put("message", message);
        mapAddress.put("address", address);
        mapAddress.put("qr", mapAddress.get("address"));

        return mapAddress;
    }

    @PostConstruct
    public void init(){

        currency = currencyService.findByName("IOTA");
        merchant = merchantService.findByName("IOTA");

        ADDRESSES = refillService.findAllAddresses(merchant.getId(), currency.getId());

        log.info("Iota starting...");
        iotaClient = new IotaAPI.Builder()
                .protocol(PROTOCOL)
                .host(HOST)
                .port(PORT)
                .build();
        log.info("Iota started");
        GetNodeInfoResponse response = iotaClient.getNodeInfo();
        System.out.println(response.toString());

        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                checkIncomingTransactions();
            }
        }, 0, 5, TimeUnit.MINUTES);

    }

    private void checkIncomingTransactions(){
        try {
            log.info("Checking IOTA transactions...");
            log.info(new java.util.Date());
            String[] stockArr = new String[ADDRESSES.size()];
            stockArr = ADDRESSES.toArray(stockArr);

            Bundle[] bundles = iotaClient.bundlesFromAddresses(stockArr,true);
            for (Bundle bundle : bundles){
                for (Transaction transaction : bundle.getTransactions()){
                    log.info(transaction.toString());
                    String addressWithChecksum = Checksum.addChecksum(transaction.getAddress());
                    if ((transaction.getValue() <= 0) || (!transaction.getPersistence())
                            || refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(addressWithChecksum,merchant.getId(),currency.getId()
                            ,transaction.getHash()).isPresent() || (!ADDRESSES.contains(addressWithChecksum))){
                        continue;
                    }

                    Map<String, String> mapPayment = new HashMap<>();
                    mapPayment.put("address", addressWithChecksum);
                    mapPayment.put("hash", transaction.getHash());
                    mapPayment.put("amount", String.valueOf(transaction.getValue()));

                    processPayment(mapPayment);

                    log.info("IOTA transaction hash-" + transaction.getHash() + ", sum-" + transaction.getValue() + " provided!");
                }
            }
            log.info(new java.util.Date());

        } catch (Exception e) {
            log.error(e);
//        } catch (ArgumentException e) {
//            log.error(e);
//        } catch (RefillRequestAppropriateNotFoundException e) {
//            log.error(e);
//        } catch (NoNodeInfoException e) {
//            log.error(e);
//        } catch (InvalidSignatureException e) {
//            log.error(e);
//        } catch (InvalidBundleException e) {
//            log.error(e);
//        } catch (NoInclusionStatesException e) {
//            log.error(e);
//        } catch (InvalidAddressException e) {
//            log.error(e);
        }
    }
}
