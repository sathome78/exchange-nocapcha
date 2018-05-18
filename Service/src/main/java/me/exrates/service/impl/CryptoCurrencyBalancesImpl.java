package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.service.BitcoinService;
import me.exrates.service.CryptoCurrencyBalances;
import me.exrates.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class CryptoCurrencyBalancesImpl implements CryptoCurrencyBalances {

    @Autowired
    Map<String, BitcoinService> bitcoinServiceMap;

    @Autowired
    MerchantService merchantService;

    public Map<Integer, String> getBalances() {

        List<Merchant> merchants = merchantService.findAll();
        Map<Integer, String> mapBalances = new HashMap<>();
        bitcoinServiceMap.forEach((k,v)-> {
            try {
                if (v.getWalletInfo().getConfirmedNonSpendableBalance().isEmpty()){
                    mapBalances.put(merchants.stream().filter(m -> m.getServiceBeanName().equals(k)).findFirst().get().getId(),v.getWalletInfo().getBalance());
                }else {
                    mapBalances.put(merchants.stream().filter(m -> m.getServiceBeanName().equals(k)).findFirst().get().getId(),v.getWalletInfo().getConfirmedNonSpendableBalance());
                }
            }catch (Exception e){
                log.error(e);
            }
        });

        return  mapBalances;
    }

}
