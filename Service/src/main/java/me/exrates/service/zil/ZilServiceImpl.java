package me.exrates.service.zil;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Conditional(MonolitConditional.class)
public class ZilServiceImpl {

    private static final String CURRENCY_NAME = "ZIL";
    private Merchant merchant;
    private Currency currency;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WithdrawUtils withdrawUtils;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(CURRENCY_NAME);
    }
}
