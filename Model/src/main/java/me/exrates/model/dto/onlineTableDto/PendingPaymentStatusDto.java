package me.exrates.model.dto.onlineTableDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;

/**
 * Created by ValkSam
 */
@Getter @Setter
public class PendingPaymentStatusDto {
  private Integer invoiceId;
  private PendingPaymentStatusEnum pendingPaymentStatus;
}
