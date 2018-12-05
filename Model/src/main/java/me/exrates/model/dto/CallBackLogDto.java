package me.exrates.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CallBackLogDto {
    private int userId;
    private int responseCode;
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
    private String requestJson;
    private String responseJson;

}
