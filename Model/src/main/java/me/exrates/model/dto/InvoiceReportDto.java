package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;

/**
 * Created by Valk
 * <p/>
 * class is used for upload data
 */
@Getter @Setter
public class InvoiceReportDto {
  private Integer docId;
  private String currency;
  private String creationDate;
  private String userEmail;
  private String recipientBank;
  private BigDecimal amount;
  private String payerName;
  private String payerBankCode;
  private String status;
  private String acceptorUserEmail;
  private String acceptanceDate;
  private String system;
  private String operation;

  public InvoiceReportDto(InvoiceRequest invoiceRequest) {
    this.docId = invoiceRequest.getTransaction().getId();
    this.currency = invoiceRequest.getTransaction().getCurrency().getName();
    this.creationDate = invoiceRequest.getTransaction().getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userEmail = invoiceRequest.getUserEmail();
    this.recipientBank = invoiceRequest.getInvoiceBank().getName();
    this.amount = invoiceRequest.getTransaction().getAmount();
    this.payerName = invoiceRequest.getUserFullName();
    this.payerBankCode = invoiceRequest.getPayerBankCode();
    this.status = ((InvoiceRequestStatusEnum) invoiceRequest.getInvoiceRequestStatus()).name();
    this.acceptorUserEmail = invoiceRequest.getAcceptanceUserEmail();
    this.acceptanceDate = invoiceRequest.getAcceptanceTime() == null ? "" : invoiceRequest.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = INPUT.name();
    this.system = TransactionSourceType.INVOICE.name();
  }

  public InvoiceReportDto(InvoiceRequestFlatForReportDto invoiceRequestFlatForReportDto) {
    this.docId = invoiceRequestFlatForReportDto.getInvoiceId();
    this.currency = invoiceRequestFlatForReportDto.getCurrency();
    this.creationDate = invoiceRequestFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userEmail = invoiceRequestFlatForReportDto.getUserEmail();
    this.recipientBank = invoiceRequestFlatForReportDto.getRecipientBank();
    this.amount = invoiceRequestFlatForReportDto.getAmount();
    this.payerName = invoiceRequestFlatForReportDto.getUserFullName();
    this.payerBankCode = invoiceRequestFlatForReportDto.getPayerBankCode();
    this.status = invoiceRequestFlatForReportDto.getStatus().name();
    this.acceptorUserEmail = invoiceRequestFlatForReportDto.getAcceptanceUserEmail();
    this.acceptanceDate = invoiceRequestFlatForReportDto.getAcceptanceTime() == null ? "" : invoiceRequestFlatForReportDto.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = INPUT.name();
    this.system = TransactionSourceType.INVOICE.name();
  }

  public InvoiceReportDto(PendingPaymentFlatForReportDto pendingPaymentFlatForReportDto) {
    this.docId = pendingPaymentFlatForReportDto.getInvoiceId();
    this.currency = pendingPaymentFlatForReportDto.getCurrency();
    this.creationDate = pendingPaymentFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userEmail = pendingPaymentFlatForReportDto.getUserEmail();
    this.recipientBank = pendingPaymentFlatForReportDto.getAddress();
    this.amount = pendingPaymentFlatForReportDto.getAmount();
    this.payerName = "";
    this.payerBankCode = "";
    this.status = pendingPaymentFlatForReportDto.getPendingPaymentStatus().name();
    this.acceptorUserEmail = pendingPaymentFlatForReportDto.getAcceptanceUserEmail();
    this.acceptanceDate = pendingPaymentFlatForReportDto.getAcceptanceTime() == null ? "" : pendingPaymentFlatForReportDto.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = INPUT.name();
    this.system = pendingPaymentFlatForReportDto.getSourceType().name();
  }

  public InvoiceReportDto(TransactionFlatForReportDto transactionFlatForReportDto) {
    this.docId = transactionFlatForReportDto.getTransactionId();
    this.currency = transactionFlatForReportDto.getCurrency();
    this.creationDate = transactionFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userEmail = transactionFlatForReportDto.getUserEmail();
    this.recipientBank = "";
    this.amount = transactionFlatForReportDto.getAmount();
    this.payerName = transactionFlatForReportDto.getMerchant();
    this.payerBankCode = transactionFlatForReportDto.getMerchant();
    this.status = transactionFlatForReportDto.getProvided() ? "PROVIDED" : "WAITING_FOR_PROVIDING";
    this.acceptorUserEmail = "";
    this.acceptanceDate = transactionFlatForReportDto.getProvidedDate() == null ? "" : transactionFlatForReportDto.getProvidedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = transactionFlatForReportDto.getOperationType().name();
    this.system = transactionFlatForReportDto.getSourceType().name();
  }

  public InvoiceReportDto(WithdrawRequestFlatForReportDto withdrawRequestFlatForReportDto) {
    this.docId = withdrawRequestFlatForReportDto.getInvoiceId();
    this.currency = withdrawRequestFlatForReportDto.getCurrency();
    this.creationDate = withdrawRequestFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userEmail = withdrawRequestFlatForReportDto.getUserEmail();
    this.recipientBank = !StringUtils.isEmpty(withdrawRequestFlatForReportDto.getRecipientBank()) ?
        withdrawRequestFlatForReportDto.getRecipientBank() :
        StringUtils.isEmpty(withdrawRequestFlatForReportDto.getWallet()) ?
            "" :
            withdrawRequestFlatForReportDto.getWallet();
    this.amount = withdrawRequestFlatForReportDto.getAmount();
    this.payerName = withdrawRequestFlatForReportDto.getMerchant();
    this.payerBankCode = withdrawRequestFlatForReportDto.getMerchant();
    this.status = withdrawRequestFlatForReportDto.getStatus().name();
    this.acceptorUserEmail = withdrawRequestFlatForReportDto.getAdminEmail();
    this.acceptanceDate = withdrawRequestFlatForReportDto.getAcceptanceTime() == null ? "" : withdrawRequestFlatForReportDto.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = OUTPUT.name();
    this.system = withdrawRequestFlatForReportDto.getSourceType().name();
  }

  public static String getTitle() {
    return "doc id" + ";" +
        "Currency" + ";" +
        "Creation date" + ";" +
        "Email" + ";" +
        "Recipient bank / Blockchain address" + ";" +
        "Amount" + ";" +
        "Payer name" + ";" +
        "Payer bank" + ";" +
        "status" + ";" +
        "Acceptor's Email" + ";" +
        "Acceptance/Change status date" + ";" +
        "Operation" + ";" +
        "System" +
        "\r\n";
  }

  @Override
  public String toString() {
    return docId + ";" +
        currency + ";" +
        creationDate + ";" +
        userEmail + ";" +
        recipientBank + ";" +
        BigDecimalProcessing.formatNoneComma(amount, false) + ";" +
        (payerName == null ? "" : payerName) + ";" +
        (payerBankCode == null ? "" : payerBankCode) + ";" +
        status + ";" +
        (acceptorUserEmail == null ? "" : acceptorUserEmail) + ";" +
        (acceptanceDate == null ? "" : acceptanceDate) + ";" +
        (operation == null ? "" : operation) + ";" +
        (system == null ? "" : system) +
        "\r\n";
  }

}
