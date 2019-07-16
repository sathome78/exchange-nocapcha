package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder")
public class QuberaRequestPaymentShortDto {

    private String senderAccountNumber;
    private String narrative;

    private BeneficiaryDetailsDto beneficiary;
    private QuberaPaymentToMasterDto paymentAmount;

    public static QuberaRequestPaymentShortDto forIban(String firstName, String lastName, String iban, String amount,
                                                       String currencyCode, String narrative, String accountNumber) {
        QuberaRequestPaymentShortDto quberaRequestPaymentShortDto = new QuberaRequestPaymentShortDto();
        quberaRequestPaymentShortDto.setNarrative(narrative);
        quberaRequestPaymentShortDto.setSenderAccountNumber(accountNumber);

        BeneficiaryAccountDto account = BeneficiaryAccountDto.builder().iban(iban).build();

        BeneficiaryDetailsDto beneficiary = BeneficiaryDetailsDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .account(account)
                .build();
        quberaRequestPaymentShortDto.setBeneficiary(beneficiary);

        QuberaPaymentToMasterDto paymentAmount =
                QuberaPaymentToMasterDto.builder().amount(new BigDecimal(amount)).currencyCode(currencyCode).build();

        quberaRequestPaymentShortDto.setPaymentAmount(paymentAmount);
        return quberaRequestPaymentShortDto;
    }


}
