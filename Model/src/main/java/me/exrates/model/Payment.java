package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.OperationType;

import javax.validation.constraints.Min;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
@NoArgsConstructor
public class Payment {
    private int currency;
    private int merchant;
    private double sum;
    private String destination;
    private int merchantImage;
    private OperationType operationType;

    public Payment(OperationType operationType) {
        this.operationType = operationType;
    }
}