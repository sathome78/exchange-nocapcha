package me.exrates.service.impl.inout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.EDCService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Log4j2(topic = "edc_log")
@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class EDCServiceMsImpl implements EDCService {

    private static final String API_MERCHANT_EDC_PROCESS_PAYMENT = "/api/merchant/edc/processPayment";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_EDC_PROCESS_PAYMENT);

        HttpEntity<String> entity = null;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(params));
        } catch (JsonProcessingException e) {
            log.error("error processPayment edc", e);
            throw new RuntimeException(e);
        }
        try {
            template.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);

        }catch (Exception ex){
            log.error("EDC coin. InOutMicroservice. Error: {}", ex);
        }

    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
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
