package me.exrates.model.dto.merchants.waves;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WavesPayment {
    private String sender;
    private String recipient;
    private Long fee;
    private Long amount;
    private String attachment = "";
}
