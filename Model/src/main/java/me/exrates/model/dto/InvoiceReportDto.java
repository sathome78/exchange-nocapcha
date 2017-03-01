package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

/**
 * Created by Valk
 * <p/>
 * class is used for upload data
 */
@Getter @Setter
public class InvoiceReportDto {
  private Integer invoiceId;
  private String creationDate;
  private String userEmail;
  private String recipientBank;
  private BigDecimal amount;
  private String payerName;
  private String payerBankCode;
  private String status;
  private String acceptorUserEmail;
  private String acceptanceDate;

  public static String getTitle() {
    return "doc id" + ";" +
        "Creation date" + ";" +
        "Email" + ";" +
        "Recipient bank" + ";" +
        "Amount" + ";" +
        "Payer name" + ";" +
        "Payer bank" + ";" +
        "status" + ";" +
        "Acceptor's Email" + ";" +
        "Acceptance date" +
        "\r\n";
  }

  @Override
  public String toString() {
    return invoiceId + ";" +
        creationDate + ";" +
        userEmail + ";" +
        recipientBank + ";" +
        BigDecimalProcessing.formatNoneComma(amount, false) + ";" +
        (payerName == null ? "" : payerName) + ";" +
        (payerBankCode == null ? "" : payerBankCode) + ";" +
        status + ";" +
        (acceptorUserEmail == null ? "" : acceptorUserEmail) + ";" +
        (acceptanceDate == null ? "" : acceptanceDate) +
        "\r\n";
  }

}
