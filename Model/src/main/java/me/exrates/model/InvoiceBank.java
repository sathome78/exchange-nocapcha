package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Created by OLEG on 02.02.2017.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceBank {

    private Integer id;
    @JsonInclude(NON_NULL)
    private Integer currencyId;
    private String name;
    private String accountNumber;
    @JsonInclude(NON_NULL)
    private String recipient;
    @JsonInclude(NON_NULL)
    private String bankDetails;



}
