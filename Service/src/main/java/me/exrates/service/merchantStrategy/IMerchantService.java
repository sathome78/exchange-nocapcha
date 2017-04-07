package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IMerchantService {
  default void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception{

  };

  RedirectView getMerchantRefillPage(RefillRequestCreateDto request);
}
