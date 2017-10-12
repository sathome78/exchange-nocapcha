package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * Created by Maks on 09.10.2017.
 */
@Data
@Builder
public class SmsSubscriptionDto {

    private int id;
    private int userId;
    private long contact;

    @Tolerate
    public SmsSubscriptionDto() {
    }
}
