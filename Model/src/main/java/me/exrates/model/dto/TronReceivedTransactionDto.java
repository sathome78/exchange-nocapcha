package me.exrates.model.dto;

import lombok.Data;
import org.json.JSONObject;

@Data
public class TronReceivedTransactionDto {

    private String amount;
    private String hash;
    /*HEX address*/
    private String address;
    /*Base58 address*/
    private String addressBase58;
    private boolean isConfirmed;

    private final static String txType = "TransferContract";


    public TronReceivedTransactionDto(String amount, String hash, String address) {
        this.amount = amount;
        this.hash = hash;
        this.address = address;
    }

    public static TronReceivedTransactionDto fromJson(JSONObject transaction) throws Exception {
        String type = transaction.getString("type");
        if (!type.equals(txType)) {
            throw new Exception("unsupported tx type");
        }
        JSONObject parameters = transaction.getJSONObject("raw_data").getJSONArray("contract").getJSONObject(0).getJSONObject("parameter").getJSONObject("value");
        String amount = parseAmount(parameters.getLong("amount"));
        return new TronReceivedTransactionDto(amount, transaction.getString("txID"), parameters.getString("to_address"));
    }

    private static String parseAmount(long amount) {
        Double normalizedAmount = amount/1000000d;
        return normalizedAmount.toString();
    }
}
