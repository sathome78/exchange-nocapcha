package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.ngExceptions.NgDashboardException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
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

    public static QuberaRequestPaymentShortDto forSepa(ExternalPaymentShortDto dto, String accountNumber) {
        QuberaRequestPaymentShortDto quberaRequestPaymentShortDto = new QuberaRequestPaymentShortDto();
        quberaRequestPaymentShortDto.setNarrative(dto.getNarrative());
        quberaRequestPaymentShortDto.setSenderAccountNumber(accountNumber);

        BeneficiaryAccountDto account = BeneficiaryAccountDto.builder().iban(dto.getIban()).build();
        BeneficiaryDetailsDto.BeneficiaryDetailsDtoBuilder builder = BeneficiaryDetailsDto.builder();

        if (StringUtils.isEmpty(dto.getCompanyName())) {
            if (StringUtils.isEmpty(dto.getFirstName()) && StringUtils.isEmpty(dto.getLastName())) {
                throw new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_FIRST_OR_LAST_NAME_NULL);
            }
            builder.firstName(dto.getFirstName()).lastName(dto.getLastName());
        } else {
            builder.companyName(dto.getCompanyName());
        }

        BeneficiaryDetailsDto beneficiary = builder.account(account).build();
        quberaRequestPaymentShortDto.setBeneficiary(beneficiary);

        QuberaPaymentToMasterDto paymentAmount =
                QuberaPaymentToMasterDto.builder()
                        .amount(new BigDecimal(dto.getAmount()))
                        .currencyCode(dto.getCurrencyCode())
                        .build();

        quberaRequestPaymentShortDto.setPaymentAmount(paymentAmount);
        return quberaRequestPaymentShortDto;
    }

    public static QuberaRequestPaymentShortDto forSwift(ExternalPaymentShortDto dto, String accountNumber) {
        QuberaRequestPaymentShortDto quberaRequestPaymentShortDto = new QuberaRequestPaymentShortDto();
        quberaRequestPaymentShortDto.setNarrative(dto.getNarrative());
        quberaRequestPaymentShortDto.setSenderAccountNumber(accountNumber);

        BeneficiaryAccountDto account = BeneficiaryAccountDto.builder().swift(dto.getSwift()).accountNumber(dto.getAccountNumber()).build();
        BeneficiaryDetailsDto.BeneficiaryDetailsDtoBuilder builder = BeneficiaryDetailsDto.builder();

        if (StringUtils.isEmpty(dto.getCompanyName())) {
            if (StringUtils.isEmpty(dto.getFirstName()) && StringUtils.isEmpty(dto.getLastName())) {
                throw new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_FIRST_OR_LAST_NAME_NULL);
            }
            builder.firstName(dto.getFirstName()).lastName(dto.getLastName());
        } else {
            builder.companyName(dto.getCompanyName());
        }

        BeneficiaryDetailsDto beneficiary = builder
                .account(account)
                .address(dto.getAddress())
                .city(dto.getCity())
                .countryCode(dto.getCountryCode())
                .build();
        quberaRequestPaymentShortDto.setBeneficiary(beneficiary);
        QuberaPaymentToMasterDto paymentAmount =
                QuberaPaymentToMasterDto.builder()
                        .amount(new BigDecimal(dto.getAmount()))
                        .currencyCode(dto.getCurrencyCode()).build();

        quberaRequestPaymentShortDto.setPaymentAmount(paymentAmount);
        return quberaRequestPaymentShortDto;
    }


}
