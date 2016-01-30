package me.exrates.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import me.exrates.config.WebAppConfig;
import me.exrates.model.Currency;
import me.exrates.service.CurrencyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ContextConfiguration(
        classes = WebAppConfig.class
)
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@WebAppConfiguration
@DatabaseSetup(value = "/init-data.xml",type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = "/init-data.xml",type = DatabaseOperation.TRUNCATE_TABLE)
public class DaoTest {

    @Autowired
    CurrencyService currencyService;


    @Test
    public void test() {
        final List<Currency> allCurrencies = currencyService.getAllCurrencies();
        allCurrencies.forEach(System.out::println);
    }
}