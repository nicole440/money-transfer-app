package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class JdbcAccountDaoTests extends BaseDaoTests {

    protected static final Account ACCOUNT_1 = new Account(2001, BigDecimal.valueOf(1000.00), 1001);
    protected static final Account ACCOUNT_2 = new Account(2002, BigDecimal.valueOf(0.00), 1002);

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getBalanceByUser_returnsExpectedBalance() {
        Account testAccount = new Account(2001, BigDecimal.valueOf(1000.00), 1001);
        User testUser = new User(1001, "user1", "user1", "USER");
        String validUser = testUser.getUsername();
        BigDecimal expectedBalance = BigDecimal.valueOf(1000.00);
        BigDecimal actualBalance = sut.getBalanceByUser(validUser);
        assertEquals(expectedBalance, actualBalance);
    }
}

