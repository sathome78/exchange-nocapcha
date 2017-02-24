package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.PendingPayment;

import java.util.Optional;

/**
 * Created by ValkSam
 */
@Getter @Setter
@NoArgsConstructor
public class PendingPaymentSimpleDto {
  private int invoiceId;
  private String transactionHash;
  private String address;
  private String hash;

  public PendingPaymentSimpleDto(PendingPayment pendingPayment){
    this.invoiceId = pendingPayment.getInvoiceId();
    this.transactionHash = pendingPayment.getTransactionHash();
    this.address = pendingPayment.getAddress();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final PendingPaymentSimpleDto that = (PendingPaymentSimpleDto) o;

    if (invoiceId != that.invoiceId) return false;
    if (transactionHash != null ? !transactionHash.equals(that.transactionHash) : that.transactionHash != null)
      return false;
    return address != null ? address.equals(that.address) : that.address == null;

  }

  @Override
  public int hashCode() {
    int result = invoiceId;
    result = 31 * result + (transactionHash != null ? transactionHash.hashCode() : 0);
    result = 31 * result + (address != null ? address.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "PendingPaymentSimpleDto{" +
        "invoiceId=" + invoiceId +
        ", transactionHash='" + transactionHash + '\'' +
        ", address='" + address + '\'' +
        '}';
  }
}
