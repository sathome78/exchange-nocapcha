package me.exrates.service.apollo;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2(topic = "apollo")
@PropertySource("classpath:/merchants/apollo.properties")
@Service
public class ApolloNodeServiceImpl implements ApolloNodeService {

    private @Value("${apollo.url}")String SEVER_URL;

    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_TRANSACTIIONS_URL = "/apl?requestType=getBlockchainTransactions&account=%s&type=0&subtype=0&executedOnly=true&includePhasingResult=true";

    public JSONArray getTransactions(String address, String lastHash) throws URISyntaxException {
        return new JSONObject(restTemplate.getForEntity(new URI(SEVER_URL.concat(String.format(GET_TRANSACTIIONS_URL, address))), String.class).getBody()).getJSONArray("transactions");
    }




}
