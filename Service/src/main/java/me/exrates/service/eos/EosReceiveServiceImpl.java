package me.exrates.service.eos;

import io.jafka.jeos.EosApi;
import io.jafka.jeos.EosApiFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.log4j.BasicConfigurator;
import org.springframework.stereotype.Service;

@Log4j2(topic = "eos_log")
@Service
public class EosReceiveServiceImpl implements EosReceiveService {

    private void init() {
        BasicConfigurator.configure();
        EosApi client = EosApiFactory.create("http://127.0.0.1:8900", //
                "http://jungle.cryptolions.io:18888",//
                "http://jungle.cryptolions.io:18888");;
        // ------------------------------------------------------------------------
        create(client);
    }
}
