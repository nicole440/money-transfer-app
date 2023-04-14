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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class JdbcTransferDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    protected static final User USER_3 = new User(1003, "user3", "user3", "USER");

    protected static final Account ACCOUNT_1 = new Account(2001, BigDecimal.valueOf(1000.00), 1001);
    protected static final Account ACCOUNT_2 = new Account(2002, BigDecimal.valueOf(1000.00), 1002);
    protected static final Account ACCOUNT_3 = new Account(2003, BigDecimal.valueOf(1000.00), 1003);
    protected static final Account ACCOUNT_4_ZERO_BALANCE = new Account(2004, BigDecimal.valueOf(0.00), 1004);

    protected static final Transfer TRANSFER_1 = new Transfer(3001, 2, 2, 1001, 1002, BigDecimal.valueOf(100.00));
    protected static final Transfer TRANSFER_2 = new Transfer(3002, 2, 2, 1002, 1003, BigDecimal.valueOf(50.00));
    protected static final Transfer TRANSFER_3 = new Transfer(3003, 2, 2, 1003, 1002, BigDecimal.valueOf(100.00));
    protected static final Transfer TRANSFER_4 = new Transfer(3004, 2, 2, 1001, 1003, BigDecimal.valueOf(50.00));

    protected static final Transfer TRANSFER_5_ZERO_BALANCE = new Transfer(3005, 2, 2, 1004, 1003, BigDecimal.valueOf(20.00));
    protected static final Transfer TRANSFER_6_NEGATIVE_AMOUNT = new Transfer(3006, 2, 2, 1003, 1004, BigDecimal.valueOf(-45.00));

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void listTransfersByUser_lists_all_transfers_for_user() {
        List<Transfer> testTransferList = sut.listTransfersByUser(1001);
        Assert.assertNotNull(testTransferList);
        int expected = 3;
        int actual = testTransferList.size();
        assertEquals(expected, actual);
    }

    @Test
    public void sendMoney_Happy_Path() throws IllegalTransferException {
        // Arrange
        boolean expected = true;
        // Act
        boolean actual = sut.sendMoney(TRANSFER_1.getUserFrom(), TRANSFER_1.getUserTo(), TRANSFER_1.getAmount());
        // Assert
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalTransferException.class)
    public void sendMoney_Returns_False_If_Sender_Has_Zero_Balance() throws IllegalTransferException {
        boolean actual = sut.sendMoney(TRANSFER_5_ZERO_BALANCE.getUserFrom(), TRANSFER_5_ZERO_BALANCE.getUserTo(), TRANSFER_5_ZERO_BALANCE.getAmount());
        Assert.assertFalse(actual);
    }

    @Test(expected = IllegalTransferException.class)
    public void sendMoney_Negative_Transfer() throws IllegalTransferException {
        sut.sendMoney(TRANSFER_6_NEGATIVE_AMOUNT.getUserFrom(), TRANSFER_6_NEGATIVE_AMOUNT.getUserTo(), TRANSFER_6_NEGATIVE_AMOUNT.getAmount());
    }

    @Test
    public void sendMoney_To_Self() throws IllegalTransferException {
        // Arrange
        boolean expected = false;
        // Act & Assert
        boolean actual = sut.sendMoney(1003, 1003, BigDecimal.valueOf(45.00));
        assertEquals(expected, actual);
    }

    @Test
    public void get_Transfer_Details_By_Id_Happy_Path() {
        // Arrange
        String expected = "Transfer ID: " + 3001 +
                " | Transfer Type ID: " + 2 +
                " | Transfer From: " + 2003 +
                " | Transfer To: " + 2004 +
                " | Amount: " + 1000.00 +
                " | Transfer Status: " + 2;
        // Act
        Transfer actual = sut.getTransferDetails(3001, 2001);
        // Assert
        assertEquals(expected, actual);
    }
}
