package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by ogolv on 30.07.2016.
 */
@Getter @Setter
@ToString
public class OrderBasicInfoDto {

    private int id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    private String currencyPairName;
    private String orderTypeName;
    private String exrate;
    private String stopRate;
    private String amountBase;
    private String orderCreatorEmail;
    private String status;
    private Integer statusId;
    private String role;
}
