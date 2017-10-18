package me.exrates.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Maks on 18.10.2017.
 */
@Data
public class NotificatorSettingAdminDto {

    private int notificatorId;
    private String name;
    private BigDecimal messagePrice;
    private BigDecimal subscribePrice;
    private boolean enabled;
}
