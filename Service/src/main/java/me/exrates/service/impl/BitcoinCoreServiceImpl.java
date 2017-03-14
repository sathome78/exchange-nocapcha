package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Transaction;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.BitcoinCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by OLEG on 14.03.2017.
 */
@Service
@PropertySource("classpath:node_config.properties")
@Log4j2
public class BitcoinCoreServiceImpl implements BitcoinCoreService {
  
  @Autowired
  private BtcdClient btcdClient;
  
  
  @PostConstruct
  public void init() throws BitcoindException, CommunicationException {
    
    Transaction transaction = btcdClient.getTransaction("c8cde4bb5cde935641eddb374a66a226d6229f8a0ee99c33750d868c090242da");
    log.debug(transaction.getAmount());
    
    
  }
  
  
 
  
}
