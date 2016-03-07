//package me.exrates.service.impl;
//
//import me.exrates.service.AlgorithmService;
//import me.exrates.service.TransactionService;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.mockito.Matchers.anyMapOf;
//import static org.mockito.Mockito.when;
//
///**
// * @author Denis Savin (pilgrimm333@gmail.com)
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
//public class PerfectMoneyServiceTest {
//
//    @Configuration
//    public static class MockConfig {
//
//    }
//
//    @InjectMocks
//    private final PerfectMoneyServiceImpl perfectMoneyService = new PerfectMoneyServiceImpl();
//
//    @Mock
//    private TransactionService transactionService;
//
//    @Spy
//    private AlgorithmService algorithmService = new AlgorithmServiceImpl();
//
//
//
//    @Before
//    public void setup() {
//        when(perfectMoneyService.computePaymentHash(anyMapOf(String.class,Object.class))).thenCallRealMethod();
//    }
//
//
//
//    @Test
//    public void test() {
//        Map<String,Object> params = new HashMap<String, Object>() {
//            {
//                put("PAYEE_ACCOUNT", "asdasd");
//            }
//        };
//        System.out.println(perfectMoneyService.computePaymentHash(params));
//    }
//
//}