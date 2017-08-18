package me.exrates.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter @Setter
@ToString
public class BotLaunchSettings {
    private Integer id;
    private Integer botId;
    private Integer currencyPairId;
    private String currencyPairName;
    private Boolean isEnabledForPair;
    @Min(value = 1, message = "{bot.launch.min}")
    @NotNull(message = "{bot.notnull}")
    private Integer launchIntervalInMinutes;
    @Min(value = 0, message = "{bot.timeout.create.min}")
    @NotNull(message = "{bot.notnull}")
    private Integer createTimeoutInSeconds;
    @Min(value = 1, message = "{bot.quantity.seq.min}")
    @NotNull(message = "{bot.notnull}")
    private Integer quantityPerSequence;
}
