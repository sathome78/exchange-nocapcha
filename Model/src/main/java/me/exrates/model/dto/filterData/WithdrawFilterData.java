package me.exrates.model.dto.filterData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

import static me.exrates.model.dto.filterData.FilterDataItem.*;

/**
 * Created by OLEG on 28.02.2017.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class WithdrawFilterData extends TableFilterData {
    private Integer requestId;
    private List<Integer> currencyIds;
    private List<Integer> merchantIds;
    private String startDate;
    private String endDate;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal commissionAmountFrom;
    private BigDecimal commissionAmountTo;
    private String wallet;
    private String recipientBank;
    private String fullName;
    private String email;




    @Override
    public void initFilterItems() {
        FilterDataItem[] items = new FilterDataItem[] {
                new FilterDataItem("request_id", "WITHDRAW_REQUEST.transaction_id =", requestId),
                new FilterDataItem("currency_ids", "TRANSACTION.currency_id IN", currencyIds, IN_FORMAT),
                new FilterDataItem("merchant_ids", "TRANSACTION.merchant_id IN", merchantIds, IN_FORMAT),
                new FilterDataItem("start_date", "TRANSACTION.datetime >", startDate, DATE_FORMAT),
                new FilterDataItem("end_date", "TRANSACTION.datetime <", endDate, DATE_FORMAT),
                new FilterDataItem("amount_from", "TRANSACTION.amount >", amountFrom),
                new FilterDataItem("amount_to", "TRANSACTION.amount <", amountTo),
                new FilterDataItem("commission_amount_from", "TRANSACTION.commission_amount >", commissionAmountFrom),
                new FilterDataItem("commission_amount_to", "TRANSACTION.commission_amount <", commissionAmountTo),
                new FilterDataItem("wallet", "WITHDRAW_REQUEST.wallet LIKE", wallet, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("recipient_bank", "WITHDRAW_REQUEST.recipient_bank_name LIKE", recipientBank, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("full_name", "WITHDRAW_REQUEST.user_full_name LIKE", fullName, LIKE_FORMAT_MIDDLE),
                new FilterDataItem("email", "USER.email LIKE", email, LIKE_FORMAT_MIDDLE)
        };
        populateFilterItemsNonEmpty(items);
    }
}
