package me.exrates.service.achain;

import me.exrates.model.dto.achain.TransactionDTO;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by Maks on 14.06.2018.
 */
public interface NodeService {

    String getMainAccountAddress();

    String getNewAddress();

    long getBlockCount();

    JSONArray getBlock(long blockNum);

    List<TransactionDTO> getTransactionsList(String account, String asset, Integer limit, String startBlock, String endBlock);

    TransactionDTO getTransaction(long blockNum, String trxId);
}
