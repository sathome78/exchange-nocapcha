package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder")
public class ExternalPaymentShortDto {
    private String firstName;
    private String lastName;
    private String iban;
    private String narrative;
    private String amount;
    private String currencyCode;
}
