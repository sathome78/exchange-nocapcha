package me.exrates.service.nem;

import lombok.extern.log4j.Log4j2;
import org.nem.core.crypto.Signer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Created by maks on 18.07.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemTransactionsService {

    private @Value("${ncc.server.url}")String nccServer;
    private @Value("${nis.server.url}")String nisServer;

    String transactionType = "0x101";
    String version_main = "0x68";
    String version_test = "0x98";

    public Signer signer;


    public void withdraw() {

    }
}
