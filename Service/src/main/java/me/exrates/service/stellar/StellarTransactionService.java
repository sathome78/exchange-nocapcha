package me.exrates.service.stellar;

import me.exrates.model.dto.WithdrawMerchantOperationDto;
import org.stellar.sdk.responses.TransactionResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by maks on 11.06.2017.
 */
public interface StellarTransactionService {
    TransactionResponse getTxByURI(String serverURI, URI txUri) throws IOException, URISyntaxException;

    Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto, String serverUrl, String accountSecret);

    String normalizeAmountToString(BigDecimal amount);

    String normalizeAmountToString(String amount);

    BigDecimal normalizeAmountToDecimal(String amount);
}
