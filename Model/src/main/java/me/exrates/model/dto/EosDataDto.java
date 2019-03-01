package me.exrates.model.dto;

import lombok.Data;
import org.json.JSONObject;

import java.math.BigDecimal;

@Data
public class EosDataDto {

    private BigDecimal amount;

    private String currency;

    private String quantity;

    private String fromAccount;

    private String toAccount;

    private String memo;


  /*  public EosDataDto(String dataObject) {
        System.out.println(dataObject);
        JSONObject object = new JSONObject(dataObject);
        String[] fullAmountString = object.getString("quantity").split(" ");
        this.amount = new BigDecimal(fullAmountString[0]);
        this.currency = fullAmountString[1];
        this.fromAccount = object.getString("from");
        this.toAccount = object.getString("to");
        this.memo = object.getString("memo");
    }*/
}
