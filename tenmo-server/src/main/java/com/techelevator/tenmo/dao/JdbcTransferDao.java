package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.IllegalTransferException;
import com.techelevator.tenmo.exceptions.InsufficientFundsException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transfer> listTransfersByUser(int userId) {
        String sql = "SELECT transfer_id, transfer.transfer_type_id, transfer.transfer_status_id, account_to, account_from, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account as sender ON transfer.account_from = sender.account_id " +
                "JOIN account as recipient ON transfer.account_to = recipient.account_id " +
                "WHERE sender.user_id = ? OR recipient.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        List<Transfer> history = new ArrayList<>();
        while (results.next()) {
            history.add(mapRowToTransfer(results, userId));
        }
        return history;
    }

    // TODO fix this process so it stops returning an account number
    @Override
    public Transfer getTransferDetails(int transferId, int userId) {
        Transfer transfer = null;
        String sql = "SELECT transfer.transfer_type_id, transfer.transfer_status_id, account_to, account_from, amount " +
                "FROM transfer " +
                "JOIN transfer_type ON transfer.transfer_type_id = transfer_type.transfer_type_id " +
                "JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id " +
                "JOIN account as sender ON transfer.account_from = sender.account_id " +
                "JOIN account as recipient ON transfer.account_to = recipient.account_id " +
                "WHERE transfer_id = ? AND (sender.user_id = ? OR recipient.user_id = ?);";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, Transfer.class, transferId, userId, userId);
        if (result.next()) {
            transfer = mapRowToTransfer(result, userId);
        }
        return transfer;
    }

    @Override
    public boolean sendMoney(int senderId, int recipientId, BigDecimal amount) throws IllegalTransferException {
        String sql = "START TRANSACTION; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE user_id = ?; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ?; " +
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?); " +
                "COMMIT; ";
        try {
            jdbcTemplate.update(sql, amount, senderId, amount, recipientId, senderId, recipientId, amount);
        } catch (DataIntegrityViolationException e) {
            sql = "ROLLBACK;";
            jdbcTemplate.update(sql);
            throw new IllegalTransferException();
        }
        return true;
    }

    @Override
    public void requestMoney(int recipientId, int senderId, BigDecimal amount) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_to, account_from, amount) " +
                "VALUES (1, 1, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?);";
        jdbcTemplate.update(sql, recipientId, senderId, amount);
    }

    // TODO add functionality to approve or reject transaction request

    @Override
    public int getUserId(String currentUserName) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        int userId = jdbcTemplate.queryForObject(sql, Integer.class, currentUserName);
        return userId;
    }

    /* Used to check whether the current user's ID matches the account ID on the transfer */
    private boolean accountIdMatchesUserId(int accountId, int userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        int returnedAccountId = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return (returnedAccountId == accountId);
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet, int userId) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        if (accountIdMatchesUserId(rowSet.getInt("account_to"), userId)) {
            transfer.setUserTo(userId);
        } else {
            transfer.setUserTo(rowSet.getInt("account_to"));
        }
        if (accountIdMatchesUserId(rowSet.getInt("account_from"), userId)) {
            transfer.setUserFrom(userId);
        } else {
            transfer.setUserFrom(rowSet.getInt("account_from"));
        }
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }
}
