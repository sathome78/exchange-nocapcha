package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class BotTrader {
    private Integer id;
    private Integer userId;
    private Boolean isEnabled;
    private Integer acceptDelayInSeconds;

}
