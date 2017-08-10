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
    private Boolean isEnabledForPair;
    @Min(value = 0, message = "Launch interval must be greater than 0")
    @NotNull(message = "Please fill in the launch interval field")
    private Integer launchIntervalInMinutes;
    @Min(value = 0, message = "Create timeout value must be greater than 0")
    @NotNull(message = "Please fill in the create timeout field")
    private Integer createTimeoutInSeconds;
    @Min(value = 0, message = "Quantity per launch value must be greater than 0")
    @NotNull(message = "Please fill in the quantity per launch field")
    private Integer quantityPerSequence;
}
