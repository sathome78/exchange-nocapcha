package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OrderType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@ToString
public class OrderHistoryItem {
    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("date_acceptance")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateAcceptance;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal price;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal total;
    @JsonProperty("order_type")
    private OrderType orderType;
}
