package me.exrates.model.dto.mobileApiDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.enums.InvoiceRequestStatusEnum;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Created by OLEG on 21.02.2017.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class InvoiceDetailsDto {

    private Integer id;
    private Integer currencyId;
    private Double amount;
    private Double commissionAmount;
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime creationTime;
    @JsonInclude(NON_NULL)
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime acceptanceTime;
    private InvoiceBank targetBank;
    @JsonInclude(NON_NULL)
    private String userFullName;
    @JsonInclude(NON_NULL)
    private String remark;
    @JsonInclude(NON_NULL)
    private String payerBankName;
    @JsonInclude(NON_NULL)
    private String payerBankCode;
    @JsonInclude(NON_NULL)
    private String payerAccount;
    @JsonInclude(NON_NULL)
    private InvoiceRequestStatusEnum invoiceRequestStatus;
    @JsonInclude(NON_NULL)
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime statusUpdateDate;
    @JsonInclude(NON_NULL)
    private String receiptScanFullPath;
    @JsonInclude(NON_NULL)
    private String receiptScanName;


    public InvoiceDetailsDto(InvoiceRequest invoiceRequest, String baseUrl) {
        this.id = invoiceRequest.getTransaction().getId();
        this.currencyId = invoiceRequest.getTransaction().getCurrency().getId();
        this.amount = invoiceRequest.getTransaction().getAmount().doubleValue();
        this.commissionAmount = invoiceRequest.getTransaction().getCommissionAmount().doubleValue();
        this.creationTime = invoiceRequest.getTransaction().getDatetime();
        this.acceptanceTime = invoiceRequest.getAcceptanceTime();
        this.targetBank = invoiceRequest.getInvoiceBank();
        this.userFullName = invoiceRequest.getUserFullName();
        this.remark = StringEscapeUtils.unescapeHtml4(invoiceRequest.getRemark());
        this.payerBankName = invoiceRequest.getPayerBankName();
        this.payerBankCode = invoiceRequest.getPayerBankCode();
        this.payerAccount = invoiceRequest.getPayerAccount();
        this.invoiceRequestStatus = invoiceRequest.getInvoiceRequestStatus();
        this.statusUpdateDate = invoiceRequest.getStatusUpdateDate();
        boolean hasReceiptPath = !StringUtils.isEmpty(invoiceRequest.getReceiptScanPath());
        if (hasReceiptPath) {
            this.receiptScanFullPath = baseUrl + invoiceRequest.getReceiptScanPath();
        }
        if (!StringUtils.isEmpty(invoiceRequest.getReceiptScanName())) {
            this.receiptScanName = invoiceRequest.getReceiptScanName();
        } else if (hasReceiptPath) {
            //Regex \\\\|/ splits path by slash (for Unix) or backslash (for Windows)
            String[] pathElements = invoiceRequest.getReceiptScanPath().split("\\\\|/");
            this.receiptScanName = pathElements[pathElements.length - 1];
        }
    }

}
