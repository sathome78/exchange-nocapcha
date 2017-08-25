package me.exrates.model.dto.merchants.lisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiskTransaction {
    private String id;
    private Integer height;
    private String blockId;
    private Integer type;
    private Long timestamp;
    private String senderId;
    private String recipientId;
    private BigDecimal amount;
    private BigDecimal fee;
    private Integer confirmations;
}
