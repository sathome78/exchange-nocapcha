package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.TronTransferDto;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Log4j2
@PropertySource("classpath:/merchants/tron.properties")
@Service
public class TronTransactionsServiceImpl implements TronTransactionsService {



    interface CommonConstant {
        byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
        byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
        int ADDRESS_SIZE = 21;
    }

    private @Value("${tron.mainAccountHEXAddress}")String MAIN_ADDRESS_HEX;


    @Autowired
    private TronNodeService tronNodeService;
    @Autowired
    private TronService tronService;
    @Autowired
    private RefillService refillService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService transferScheduler = Executors.newScheduledThreadPool(1);

    private void init() {
        scheduler.scheduleAtFixedRate(this::checkUnconfirmedJob, 1, 10, TimeUnit.MINUTES);
        transferScheduler.scheduleAtFixedRate(this::transferToMainAccountJob, 1, 20, TimeUnit.MINUTES);
    }

    private void checkUnconfirmedJob() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(tronService.getMerchantId(), tronService.getCurrencyId());
        dtos.forEach(p->{
            if (checkIsTransactionConfirmed(p.getMerchantTransactionId())) {
                processTransaction(p.getAddress(), p.getMerchantTransactionId(), p.getAmount().toString());
            }
        });

    }

    private void transferToMainAccountJob() {
        List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(tronService.getMerchantId(), tronService.getCurrencyId());
        listRefillRequestAddressDto.forEach(p->{
            transferToMainAccount(p);
            refillService.updateAddressNeedTransfer(p.getAddress(), tronService.getMerchantId(), tronService.getCurrencyId(), false);
        });
    }

    private void transferToMainAccount(RefillRequestAddressDto dto) {
        Long accountAmount = tronNodeService.getAccount(dto.getAddress()).getJSONObject("data").getLong("balance");
        easyTransferByPrivate(MAIN_ADDRESS_HEX, dto.getPubKey(), accountAmount);
    }

    @Override
    public boolean checkIsTransactionConfirmed(String txHash) {
        JSONObject rawResponse = tronNodeService.getTransaction(txHash);
        return rawResponse.getBoolean("confirmed");
    }

    @Override
    public void processTransaction(TronReceivedTransactionDto p) {
        processTransaction(p.getAddressBase58(), p.getHash(), p.getAmount());
    }

    @Override
    public void processTransaction(String address, String hash, String amount) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("hash", hash);
        map.put("amount", amount);
        try {
            tronService.processPayment(map);
            refillService.updateAddressNeedTransfer(address, tronService.getMerchantId(), tronService.getCurrencyId(), true);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("request not found {}", address);
        }
    }



    private void easyTransferByPrivate(String pk, String addressTo, long amount) {
        TronTransferDto tronTransferDto = new TronTransferDto(pk, addressTo, 1000000L);
    }



    public static byte[] decodeFromBase58Check(String addressBase58) {
        if (StringUtils.isEmpty(addressBase58)) {
            log.warn("Warning: Address is empty !!");
            return null;
        }
        byte[] address = decode58Check(addressBase58);
        if (!addressValid(address)) {
            return null;
        }
        return address;
    }

    public static String encode58Check(byte[] input) {
        byte[] hash0 = Sha256Hash.hash(input);
        byte[] hash1 = Sha256Hash.hash(hash0);
        byte[] inputCheck = new byte[input.length + 4];
        System.arraycopy(input, 0, inputCheck, 0, input.length);
        System.arraycopy(hash1, 0, inputCheck, input.length, 4);
        return Base58.encode(inputCheck);
    }

    private static byte[] decode58Check(String input) {
        byte[] decodeCheck = Base58.decode(input);
        if (decodeCheck.length <= 4) {
            return null;
        }
        byte[] decodeData = new byte[decodeCheck.length - 4];
        System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
        byte[] hash0 = Sha256Hash.hash(decodeData);
        byte[] hash1 = Sha256Hash.hash(hash0);
        if (hash1[0] == decodeCheck[decodeData.length] &&
                hash1[1] == decodeCheck[decodeData.length + 1] &&
                hash1[2] == decodeCheck[decodeData.length + 2] &&
                hash1[3] == decodeCheck[decodeData.length + 3]) {
            return decodeData;
        }
        return null;
    }

    public static boolean addressValid(byte[] address) {
        if (ArrayUtils.isEmpty(address)) {
            log.warn("Warning: Address is empty !!");
            return false;
        }
        if (address.length != CommonConstant.ADDRESS_SIZE) {
            log.warn(
                    "Warning: Address length need " + CommonConstant.ADDRESS_SIZE + " but " + address.length
                            + " !!");
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != CommonConstant.ADD_PRE_FIX_BYTE_MAINNET) {
            log.warn("Warning: Address need prefix with " + CommonConstant.ADD_PRE_FIX_BYTE_MAINNET + " but "
                            + preFixbyte + " !!");
            return false;
        }
        //Other rule;
        return true;
    }


}
