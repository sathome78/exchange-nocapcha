package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.enums.AlertType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by Maks on 13.12.2017.
 */
@Data
@Builder(toBuilder = true)
public class AlertDto {

    private String text;
    private String alertType;
    private boolean enabled;
    private LocalDateTime eventStart;
    private LocalTime lenghtOfWorks;
    @JsonIgnore
    private LocalDateTime launchDateTime;
}
