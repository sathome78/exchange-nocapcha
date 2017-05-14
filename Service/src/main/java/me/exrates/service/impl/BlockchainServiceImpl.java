package me.exrates.service.impl;

import me.exrates.dao.BTCTransactionDao;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.BlockchainSDKWrapper;
import me.exrates.service.BlockchainService;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.StringJoiner;

import static java.util.Objects.isNull;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */

@Service
@PropertySource("classpath:/merchants/blockchain.properties")
public class BlockchainServiceImpl implements BlockchainService {

    private @Value("${xPub}") String xPub;
    private @Value("${apiCode}") String apiCode;
    private @Value("${callbackUrl}") String callbackUrl;
    private @Value("${secret}") String secret;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private BTCTransactionDao btcTransactionDao;

    @Autowired
    private BlockchainSDKWrapper blockchainSDKWrapper;

    private static final Logger LOG = LogManager.getLogger("merchant");
    private static final BigDecimal SATOSHI = new BigDecimal(100_000_000L);
    private static final int decimalPlaces = 8;

    //TODO REFILL

    private String computeTransactionHash(final Transaction request) {
        if (isNull(request) || isNull(request.getCommission()) || isNull(request.getCommissionAmount())) {
            throw new IllegalArgumentException("Argument itself or contain null");
        }
        final String target = new StringJoiner(":")
            .add(String.valueOf(request.getId()))
            .add(secret)
            .toString();
        return algorithmService.sha256(target);
    }

}
