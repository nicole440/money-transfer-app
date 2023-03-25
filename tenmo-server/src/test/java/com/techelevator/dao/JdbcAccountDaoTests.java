package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountDaoTests extends BaseDaoTests {

    protected static final Account ACCOUNT_1 = new Account(2001, BigDecimal.valueOf(1000.00), 1001);
    protected static final Account ACCOUNT_2 = new Account(2002, BigDecimal.valueOf(0.00), 1002);
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void get_Balance_By_Id_Happy_Path() {
        // Arrange
        BigDecimal expected = new BigDecimal(1000.00);
        // Act
        BigDecimal actual = sut.getBalanceByUser("user1");
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testing_Invalid_User() {
        String testUser = "Ted";
        BigDecimal actual = sut.getBalanceByUser(testUser);
    }
}
