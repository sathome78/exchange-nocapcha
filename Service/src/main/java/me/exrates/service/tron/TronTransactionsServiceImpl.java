package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.TronTransferDto;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

interface CommonConstant {
    byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
    byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
    int ADDRESS_SIZE = 21;
}


@Log4j2
@Service
public class TronTransactionsServiceImpl {

    @Autowired
    private TronNodeService tronNodeService;


    public void easyTransferByPrivate(String address, String pk, long amount) {
        TronTransferDto tronTransferDto = new TronTransferDto(ByteArray.fromHexString(pk), decodeFromBase58Check(address), 1000000L);
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
