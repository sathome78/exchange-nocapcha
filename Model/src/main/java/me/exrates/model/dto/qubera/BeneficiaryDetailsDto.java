package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryDetailsDto {
    private String address;
    private String city;
    private String companyName;
    private String countryCode;
    private String zipCode;
    private String firstName;
    private String lastName;


}
