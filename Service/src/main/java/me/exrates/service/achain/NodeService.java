package me.exrates.service.achain;

import me.exrates.model.dto.achain.TransactionDTO;
import org.json.JSONArray;

/**
 * Created by Maks on 14.06.2018.
 */
public interface NodeService {

    String getMainAccountAddress();

    String getNewAddress();

    long getBlockCount();

    JSONArray getBlock(long blockNum);

    TransactionDTO getTransaction(long blockNum, String trxId);
}
