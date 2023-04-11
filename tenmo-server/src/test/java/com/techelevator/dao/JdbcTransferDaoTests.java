package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exceptions.IllegalTransferException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");

    protected static final Account ACCOUNT_1 = new Account(2001, BigDecimal.valueOf(1000.00), 1001);
    protected static final Account ACCOUNT_2 = new Account(2002, BigDecimal.valueOf(0.00), 1002);
    protected static final Transfer TRANSFER_1 = new Transfer(3001, 2, 2, 2003, 2004, BigDecimal.valueOf(1000.00));
    protected static final Transfer TRANSFER_2 = new Transfer(3002, 2, 2, 2003, 2003, BigDecimal.valueOf(46.00));
    protected static final Transfer TRANSFER_3 = new Transfer(3046, 2, 2, 2002, 2001, BigDecimal.valueOf(10.00));
    protected static final Transfer TRANSFER_4 = new Transfer(3046, 2, 2, 2003, 2001, BigDecimal.valueOf(-10.00));
    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void list_Transfers_Happy_Path() {
        List<Transfer> testTransferList = sut.listTransfersByUser(1001);
        Assert.assertNotNull(testTransferList);
        Assert.assertEquals(1, testTransferList.size());
    }

    @Test
    public void send_Money_Happy_Path() {
        // Arrange
        boolean expected = true;
        // Act
        boolean actual = sut.sendMoney(TRANSFER_1.getUserFrom(), TRANSFER_1.getUserTo(), TRANSFER_1.getAmount());
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = InsufficientFundsException.class)
    public void send_Money_Zero_Balance() {
        sut.sendMoney(TRANSFER_3.getUserFrom(), TRANSFER_3.getUserTo(), TRANSFER_3.getAmount());
    }

    @Test(expected = IllegalTransferException.class)
    public void send_Money_Negative_Transfer() {
        sut.sendMoney(TRANSFER_4.getUserFrom(), TRANSFER_4.getUserTo(), TRANSFER_4.getAmount());
    }

    @Test
    public void send_Money_To_Self() {
        // Arrange
        boolean expected = false;
        // Act
        boolean actual = sut.sendMoney(TRANSFER_2.getUserFrom(), TRANSFER_2.getUserTo(), TRANSFER_2.getAmount());
        // Assert
        Assert.assertEquals(expected, actual);
    }

//    @Test
//    public void get_Transfer_Details_By_Id_Happy_Path() {
//        // Arrange
//        String expected = "Transfer ID: " + 3001 +
//                " | Transfer Type ID: " + 2 +
//                " | Transfer From: " + 2003 +
//                " | Transfer To: " + 2004 +
//                " | Amount: " + 1000.00 +
//                " | Transfer Status: " + 2;
//        // Act
//        Transfer actual = sut.getTransferDetails(3001, 2001);
//        // Assert
//        Assert.assertEquals(expected, actual);
//    }
}
