package me.exrates.model;

import lombok.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Merchant {
    private int id;
    private String name;
    private String description;
    private Integer transactionSourceTypeId;
    private  String serviceBeanName;
    private Boolean simpleInvoice;
    private Integer refillOperationCountLimitForUserPerDay;

    public Merchant(int id) {
        this.id = id;
    }

    public Merchant(int id, String name, String description, Integer transactionSourceTypeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.transactionSourceTypeId = transactionSourceTypeId;
    }
}