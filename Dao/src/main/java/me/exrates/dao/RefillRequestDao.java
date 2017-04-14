package me.exrates.dao;

import me.exrates.model.InvoiceBank;
import me.exrates.model.dto.RefillRequestCreateDto;

import java.util.List;

/**
 * created by ValkSam
 */
public interface RefillRequestDao {

  int findActiveRequestsByMerchantIdAndUserIdForCurrentDate(Integer merchantId, Integer userId);

  int create(RefillRequestCreateDto request);

  List<InvoiceBank> findInvoiceBanksByCurrency(Integer currencyId);
}
