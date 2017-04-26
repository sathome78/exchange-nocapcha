package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestNotFountException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IMerchantService {
  void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception;

  Map<String, String> refill(RefillRequestCreateDto request);

  void processPayment(Map<String, String> params) throws RefillRequestNotFountException;

  default String generateFullUrl(String url, Properties properties){
    return url.concat("?").concat(
        properties.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"))
    );
  }
}
