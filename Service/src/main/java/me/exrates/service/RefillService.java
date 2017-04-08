package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.vo.WithdrawData;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Locale;
import java.util.Map;

/**
 * @author ValkSam
 */
public interface RefillService {

  RedirectView createRefillRequestAndGetPageOfMerchant(RefillRequestCreateDto requestCreateDto);

}
