package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.InvoiceRequestStatusEnum;


/**
 * Created by ValkSam
 */
@Getter @Setter
@NoArgsConstructor
public class InvoiceRequestStatus {
  private Integer id;

  private String name;

  public InvoiceRequestStatus(InvoiceRequestStatusEnum invoiceRequestStatusEnum) {
    this.id = invoiceRequestStatusEnum.getCode();
    this.name = invoiceRequestStatusEnum.name();
  }

}
