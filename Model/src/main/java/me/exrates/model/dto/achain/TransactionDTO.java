package me.exrates.model.dto.achain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Maks on 14.06.2018.
 */
@Data
public class TransactionDTO implements Serializable {

    private static final long serialVersionUID = -8050539202820124272L;

    private String trxId;

    private String contractId;

    private String eventType;

    private String eventParam;

    private Long blockNum;

    private Date trxTime;

    /**
     * Call the method name
     */
    private String callAbi;

    private String fromAddr;

    private Long amount;

    /**
     * Call parameters
     */
    private String apiParams;

}
