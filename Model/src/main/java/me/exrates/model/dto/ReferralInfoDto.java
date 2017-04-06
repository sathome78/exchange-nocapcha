package me.exrates.model.dto;

import lombok.Data;

/**
 * Created by maks on 05.04.2017.
 */
@Data
public class ReferralInfoDto {

    int refId;

    String email;

    double refProfitFromUser;

    int firstRefLevelCount;
}
