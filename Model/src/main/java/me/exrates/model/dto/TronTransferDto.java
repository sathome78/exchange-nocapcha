package me.exrates.model.dto;

import lombok.Data;

@Data
public class TronTransferDto {

    /*private key of sender account*/
    private String privateKey;
    /*hex address to*/
    private String toAddress;

    private Long amount;
}
