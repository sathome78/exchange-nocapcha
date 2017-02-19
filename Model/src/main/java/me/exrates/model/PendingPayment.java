 package me.exrates.model;

 import com.fasterxml.jackson.databind.annotation.JsonSerialize;
 import lombok.Getter;
 import lombok.Setter;
 import me.exrates.model.enums.invoice.InvoiceStatus;
 import me.exrates.model.serializer.LocalDateTimeSerializer;

 import java.time.LocalDateTime;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
public class PendingPayment {
    private int invoiceId;
    private String transactionHash;
    private String address;
    private Integer userId;
    private String userEmail;
    private Integer acceptanceUserId;
    private String acceptanceUserEmail;
    private String hash;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime acceptanceTime;
    private Transaction transaction;
    private InvoiceStatus pendingPaymentStatus;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime statusUpdateDate;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PendingPayment that = (PendingPayment) o;

        if (transaction != null ? !transaction.equals(that.transaction) : that.transaction != null) return false;
        if (invoiceId != that.invoiceId) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userEmail != null ? !userEmail.equals(that.userEmail) : that.userEmail != null) return false;
        if (acceptanceUserId != null ? !acceptanceUserId.equals(that.acceptanceUserId) : that.acceptanceUserId != null)
            return false;
        if (acceptanceUserEmail != null ? !acceptanceUserEmail.equals(that.acceptanceUserEmail) : that.acceptanceUserEmail != null)
            return false;
        if (acceptanceTime != null ? !acceptanceTime.equals(that.acceptanceTime) : that.acceptanceTime != null)
            return false;
        if (transactionHash != null ? !transactionHash.equals(that.transactionHash) : that.transactionHash != null)
            return false;
        if (pendingPaymentStatus != null ? !pendingPaymentStatus.equals(that.pendingPaymentStatus) : that.pendingPaymentStatus != null)
            return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null)
            return false;
        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        int result = invoiceId;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (transactionHash != null ? transactionHash.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (pendingPaymentStatus != null ? pendingPaymentStatus.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (acceptanceUserId != null ? acceptanceUserId.hashCode() : 0);
        result = 31 * result + (acceptanceUserEmail != null ? acceptanceUserEmail.hashCode() : 0);
        result = 31 * result + (acceptanceTime != null ? acceptanceTime.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PendingPayment{" +
            "invoiceId=" + invoiceId +
            ", transactionHash='" + transactionHash + '\'' +
            ", address='" + address + '\'' +
            '}';
    }
}
