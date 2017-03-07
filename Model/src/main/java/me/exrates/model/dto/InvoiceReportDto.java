package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static me.exrates.model.enums.OperationType.INPUT;

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
    this.acceptanceDate = invoiceRequest.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.operation = INPUT.name();
    this.system = TransactionSourceType.INVOICE.name();
  }

  public InvoiceReportDto(InvoiceRequestFlatForReportDto InvoiceRequestFlatForReportDto) {
    this.docId = InvoiceRequestFlatForReportDto.getInvoiceId();
    this.currency = InvoiceRequestFlatForReportDto.getCurrency();
    this.creationDate = InvoiceRequestFlatForReportDto.getDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    this.userEmail = InvoiceRequestFlatForReportDto.getUserEmail();
    this.recipientBank = InvoiceRequestFlatForReportDto.getRecipientBank();
    this.amount = InvoiceRequestFlatForReportDto.getAmount();
    this.payerName = InvoiceRequestFlatForReportDto.getUserFullName();
    this.payerBankCode = InvoiceRequestFlatForReportDto.getPayerBankCode();
    this.status = InvoiceRequestFlatForReportDto.getStatus().name();
    this.acceptorUserEmail = InvoiceRequestFlatForReportDto.getAcceptanceUserEmail();
    this.acceptanceDate = InvoiceRequestFlatForReportDto.getAcceptanceTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
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
        "Acceptance date" + ";" +
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
