package me.exrates.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(locations = "classpath:dao-tests.xml")
public class ApiAuthTokenDaoTest {

    @Test
    public void createToken() {
    }

    @Test
    public void retrieveTokenById() {
    }

    @Test
    public void prolongToken() {
    }

    @Test
    public void deleteExpiredToken() {
    }

    @Test
    public void deleteAllExpired() {
    }
}