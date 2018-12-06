package me.exrates.model.dto.qiwi.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QiwiRequestGetTransactions {
    private int start=0;
    private int limit=100;
    private String[] tx_status;
    private String[] tx_type;
    private String[] order_id;

    public QiwiRequestGetTransactions(){}

    public QiwiRequestGetTransactions(int start, int limit){
        this.start = start;
        this.limit = limit;
    }
}
