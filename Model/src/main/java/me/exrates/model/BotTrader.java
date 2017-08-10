package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter @Setter
@ToString
public class BotTrader {
    private Integer id;
    private Integer userId;
    private Boolean isEnabled;
    @Min(value = 0, message = "Accept timeout must be greater than 0")
    @NotNull(message = "Please fill in the accept timeout field")
    private Integer acceptDelayInSeconds;

}
