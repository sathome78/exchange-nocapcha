package me.exrates.service;

import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
}
