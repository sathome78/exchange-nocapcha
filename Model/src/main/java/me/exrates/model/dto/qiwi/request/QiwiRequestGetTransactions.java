package me.exrates.model.dto.qiwi.request;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("classpath:/merchants/qiwi.properties")
public class QiwiRequestGetTransactions {
    @Value("${qiwi.transaction.position.start}")
    private int start;
    @Value("${qiwi.transaction.limit}")
    private int limit;
}
