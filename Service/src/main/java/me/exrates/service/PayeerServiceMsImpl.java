package me.exrates.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Log4j2
@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class PayeerServiceMsImpl implements PayeerService {

    private static final String API_MERCHANT_PAYEER_PROCESS_PAYMENT = "/api/merchant/payeer/processPayment";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_PAYEER_PROCESS_PAYMENT);

        HttpEntity<String> entity;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(params));
        } catch (JsonProcessingException e) {
            log.error("Payeer can't map params", e);
            throw new RuntimeException(e);
        }
        template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, String.class);

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }
}
