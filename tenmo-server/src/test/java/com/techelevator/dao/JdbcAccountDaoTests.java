package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import java.security.Principal;

import static org.junit.Assert.assertEquals;

public class JdbcAccountDaoTests extends BaseDaoTests {

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getBalanceByUser_returnsExpectedBalance() {
        User testUser = new User(1001, "user1", "user1", "USER");
        Account testAccount = new Account(2001, BigDecimal.valueOf(1000.00), 1001);
        Principal mockPrincipal = new MockPrincipal(testUser.getUsername());
        BigDecimal expectedBalance = new BigDecimal("1000.00");
        BigDecimal actualBalance = sut.getBalanceByUser(mockPrincipal.getName());
        assertEquals(expectedBalance, actualBalance);
    }
}

