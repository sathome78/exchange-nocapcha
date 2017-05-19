package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IMerchantService {
  Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception;

  /**
   * КОНТРАКТ: если методу необходим id заявки, то в случае его отсутствия необходимо выкинуть RefillRequestIdNeededException
   *
   *  Integer requestId = request.getId();
   *  if (requestId == null) {
   *    throw new RefillRequestIdNeededException(request.toString());
   *  }
   *
   */
  Map<String, String> refill(RefillRequestCreateDto request) throws RefillRequestIdNeededException;

  void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException;

  default String generateFullUrl(String url, Properties properties){
    return url.concat("?").concat(
        properties.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"))
    );
  }

  default String getMainAddress() {
    return "qwqwqqqw";
  }
}
