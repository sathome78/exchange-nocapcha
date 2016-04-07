package me.exrates.service.impl;

import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.service.AlgorithmService;
import me.exrates.service.BlockchainService;
import me.exrates.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
@TestPropertySource
public class BlockchainServiceImplTest  {

    @Mock
    TransactionService transactionService;

    @Mock
    PendingPaymentDao pendingPaymentDao;

    @Mock
    BTCTransactionDao btcTransactionDao;

    @Mock
    AlgorithmService algorithmService;

    @InjectMocks
    BlockchainService blockchainService = new BlockchainServiceImpl();

    private final String fakeXPub = "fakeXPub";
    private final String fakeApi = "api";
    private final String fakeCallback = "Callback";

    @Before
    public void setup() {
        final Field xPub = findField(BlockchainServiceImpl.class, "xPub");
        xPub.setAccessible(true);
        ReflectionUtils.setField(xPub,blockchainService,"");
    }

    @Test
    public void testComputeTransactionHash() {
        System.out.println(blockchainService);
        final Field xPub = findField(BlockchainServiceImpl.class, "xPub");
        xPub.setAccessible(true);
        final Object field = getField(xPub, blockchainService);
        System.out.println(field);
    }




}
