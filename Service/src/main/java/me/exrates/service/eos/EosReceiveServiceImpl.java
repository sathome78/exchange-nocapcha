package me.exrates.service.eos;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jafka.jeos.EosApi;
import io.jafka.jeos.EosApiFactory;
import io.jafka.jeos.core.response.chain.Block;
import io.jafka.jeos.core.response.chain.code.Action;
import io.jafka.jeos.core.response.history.action.Actions;
import io.jafka.jeos.core.response.history.transaction.Transaction;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.EosDataDto;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Log4j2(topic = "eos_log")
@Service
public class EosReceiveServiceImpl implements EosReceiveService {

    private EosApi client;
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";

    private static final String MERCHANT_NAME = "EOS";
    private static final String CURRENCY_NAME = "EOS";

    private static final String TRANSFER = "transfer";

    private String mainAccount = "";

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @PostConstruct
    private void init() {
        BasicConfigurator.configure();
        client = EosApiFactory.create("http://127.0.0.1:8900", //
                "https://api.eosnewyork.io",//
                "https://api.eosnewyork.io");
    }


    public void checkRefills() {
        Block block = client.getBlock(loadLastBlock().toString());
        List<Transaction> transactionList = Arrays.asList(block.getTransactions());
        transactionList.forEach(p -> {
            p.getTrx().ifPresent(s->{
                List<io.jafka.jeos.core.common.Action> actions = s.getTransaction().getActions();
                actions.forEach(a->{
                    String operation = a.getName();
                    if (operation.equalsIgnoreCase(TRANSFER)) {
                        String data = String.valueOf(a.getData());


                        System.out.println(data);

                    }
                });
            });
        });
    }




    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }

    private Long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }



    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        BasicConfigurator.configure();
        EosApi client = EosApiFactory.create("http://127.0.0.1:8900", //
                "https://api.eosnewyork.io",//
                "https://api.eosnewyork.io");
        // ------------------------------------------------------------------------

        Block block = client.getBlock("14643107");
        List<Transaction> transactionList = Arrays.asList(block.getTransactions());
        transactionList.forEach(System.out::println);
        transactionList.forEach(p -> {
            p.getTrx().ifPresent(s->{
                List<io.jafka.jeos.core.common.Action> actions = s.getTransaction().getActions();
                actions.forEach(a->{
                    String operation = a.getName();
                    if (operation.equalsIgnoreCase(TRANSFER)) {
                        EosDataDto dataDto = null;
                        try {
                            dataDto = objectMapper.readValue(a.getData().toString(), EosDataDto.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(dataDto);
                    }
                });

            });
        });
    }
}
