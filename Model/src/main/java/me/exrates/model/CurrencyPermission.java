package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;

/**
 * Created by OLEG on 28.02.2017.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class CurrencyPermission {
    private Currency currency;
    private InvoiceOperationPermission invoiceOperationPermission;
}
