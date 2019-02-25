package me.exrates.dao.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.dao.configuration.TestConfiguration;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class, InputOutputDaoImplTest.InnerConf.class})
public class InputOutputDaoImplTest {

    private static final String EMAIL = "shvets.k@gmail.com";
    private static final LocalDate START_DATE = LocalDate.now().minusMonths(6);
    private static final LocalDate END_DATE = LocalDate.now();
    private static final LocalDate FUTURE_START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate FUTURE_END_DATE = LocalDate.now().plusDays(2);
    private static final List<Integer> OPERATION_TYPE_LIST = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Autowired
    private InputOutputDao inputOutputDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findMyInputOutputHistoryByOperationTypeTest_WithResult() {
        List<MyInputOutputHistoryDto> history = inputOutputDao.findMyInputOutputHistoryByOperationType(EMAIL, 0, 0,
                START_DATE, END_DATE, OPERATION_TYPE_LIST, Locale.ENGLISH, 0);

        assertNotNull(history);
        assertFalse(history.isEmpty());
    }

    @Test
    public void findMyInputOutputHistoryByOperationTypeTest_WithoutResult() {
        List<MyInputOutputHistoryDto> history = inputOutputDao.findMyInputOutputHistoryByOperationType(EMAIL, 0, 0,
                FUTURE_START_DATE, FUTURE_END_DATE, OPERATION_TYPE_LIST, Locale.ENGLISH, 0);

        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Configuration
    static class InnerConf {

        @Bean
        public InputOutputDao inputOutputDao() {
            return new InputOutputDaoImpl();
        }

        @Bean
        public MessageSource messageSource() {
            return Mockito.mock(MessageSource.class);
        }
    }
}