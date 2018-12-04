package me.exrates.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CallBackLogDto {
    private long userId;
    private int responseCode;
    private String requestDate;
    private String responseDate;
    private String requestJson;
    private String responseJson;

}
