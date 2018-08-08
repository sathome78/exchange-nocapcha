package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.TronNewAddressDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Log4j2
@Service
@PropertySource("classpath:/merchants/tron.properties")
public class TronNodeServiceImpl implements TronNodeService {

    @Autowired
    private RestTemplate restTemplate;

    private @Value("${tron.full_node.url}")String FULL_NODE_URL;
    private @Value("${tron.solidity_node_url}")String SOLIDITY_NODE_URL;
    private @Value("${tron.mainAccountAddress}")String MAIN_ADDRESS;


    private final static String GET_ADDRESS = "/wallet/generateaddress";
    private final static String EASY_TRANSFER = "/wallet/easytransferbyprivate";


    @Override
    public TronNewAddressDto getNewAddress() {
        String url = FULL_NODE_URL.concat(GET_ADDRESS);
        System.out.println("url " + url);
        return TronNewAddressDto.fromGetAddressMethod(restTemplate.postForObject(url, null, String.class));
    }

    @Override
    public TronNewAddressDto transferFunds(String privatekeyFromAcc, String addressTo, BigInteger amount) {
        String url = FULL_NODE_URL.concat(EASY_TRANSFER);
        System.out.println("url " + url);
        return TronNewAddressDto.fromGetAddressMethod(restTemplate.postForObject(url, null, String.class));
    }
}
