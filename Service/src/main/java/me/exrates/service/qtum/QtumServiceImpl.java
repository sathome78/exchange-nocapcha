package me.exrates.service.qtum;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.qtum.QtumListTransactions;
import me.exrates.model.dto.merchants.qtum.QtumTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.vo.ProfileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2(topic = "qtum_log")
@Service("qtumServiceImpl")
@PropertySource("classpath:/merchants/qtum.properties")
public class QtumServiceImpl implements QtumService {

    @Autowired
    private QtumNodeService qtumNodeService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private RefillService refillService;

    private @Value("${qtum.min.confirmations}") Integer minConfirmations;

    private @Value("${qtum.min.transfer.amount}") Integer minTransferAmount;

    private @Value("${qtum.main.address.for.transfer}") String mainAddressForTransfer;

    private Merchant merchant;

    private Currency currency;

    private final String neoSpecParamName = "LastRecievedBlock";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        merchant = merchantService.findByName("Qtum");
        currency = currencyService.findByName("QTUM");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                scanBlocks();
            } catch (Exception e) {
                log.error(e);
            }

        }, 1L, 5L, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkBalanceAndTransfer();
            } catch (Exception e) {
                log.error(e);
            }

        }, 20L, 20L, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                backupWallet();
            } catch (Exception e) {
                log.error(e);
            }

        }, 1L, 12L, TimeUnit.HOURS);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = qtumNodeService.getNewAddress();

        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("message", message);
            put("address", address);
            put("qr", address);
        }};    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(params.get("address"))
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(new BigDecimal(params.get("amount")))
                .merchantTransactionId(params.get("hash"))
                .build();

        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return new HashMap<>();
    }

    @Synchronized
    private void scanBlocks() {

        log.debug("Start scanning blocks");
        ProfileData profileData = new ProfileData(500);

        final int lastReceivedBlock = Integer.parseInt(specParamsDao.getByMerchantIdAndParamName(merchant.getName(),
                neoSpecParamName).getParamValue());
        Set<String> addresses = refillService.findAllAddresses(merchant.getId(), currency.getId()).stream().distinct().collect(Collectors.toSet());

        String blockNumberHash = qtumNodeService.getBlockHash(lastReceivedBlock);

        qtumNodeService.listSinceBlock(blockNumberHash).get().getTransactions().stream()
                .filter(t -> addresses.contains(t.getAddress()))
                .filter(t -> !refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(t.getAddress(),merchant.getId(),currency.getId(),t.getTxid()).isPresent())
                .filter(t -> t.getCategory().equals("receive"))
                .filter(t -> t.getConfirmations() >= minConfirmations)
                .filter(t -> t.getWalletconflicts().size() == 0)
                .filter(t -> t.isTrusted())
                .filter(t -> t.getAmount() > 0)
                .forEach(t -> {
                            try {
                                Map<String, String> mapPayment = new HashMap<>();
                                mapPayment.put("address", t.getAddress());
                                mapPayment.put("hash", t.getTxid());
                                mapPayment.put("amount", String.valueOf(t.getAmount()));
                                processPayment(mapPayment);

                                specParamsDao.updateParam(merchant.getName(), neoSpecParamName, String.valueOf(qtumNodeService.getBlock(t.getBlockhash()).getHeight()));
                            }catch (Exception e){
                                log.error(e);
                            }
                        }
                );
        profileData.setTime1();
        log.debug("Profile results: " + profileData);
    }

    private void checkBalanceAndTransfer(){
        log.debug("Start checking balance");
        ProfileData profileData = new ProfileData(500);

        qtumNodeService.setWalletPassphrase();

        BigDecimal balance = qtumNodeService.getBalance();
        if (balance.compareTo(BigDecimal.valueOf(minTransferAmount)) > 0){
            qtumNodeService.transfer(mainAddressForTransfer, balance.subtract(new BigDecimal("0.01")));
        }
        profileData.setTime1();
        log.debug("Profile results: " + profileData);
    }

    private void backupWallet(){
        log.debug("Start backup wallet");
        ProfileData profileData = new ProfileData(500);

        qtumNodeService.setWalletPassphrase();

        qtumNodeService.backupWallet();

        profileData.setTime1();
        log.debug("Profile results: " + profileData);
    }


}
