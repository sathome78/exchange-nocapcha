package me.exrates.service;

import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ValkSam on 02.06.2017.
 */
public interface TransferService {
  Map<String, Object> createTransferRequest(TransferRequestCreateDto request);

  List<MerchantCurrency> retrieveAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies);

  void revokeTransferRequest(int requestId);

  List<TransferRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses);

  TransferRequestFlatDto getFlatById(Integer id);

  Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale);

  Optional<TransferRequestFlatDto> getByHashAndStatus(String code, Integer requiredStatus, boolean block);

  boolean checkRequest(TransferRequestFlatDto transferRequestFlatDto, Principal principal);

  void performTransfer(TransferRequestFlatDto transferRequestFlatDto, Locale locale, InvoiceActionTypeEnum action);

    String getUserEmailByTrnasferId(int id);
}
