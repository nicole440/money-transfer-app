package com.techelevator.tenmo.dao;

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

    // SQL tested via pgAdmin query: SUCCESS
    @Override
    public List<Transfer> listTransfersByUser(int userId) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_to OR account.account_id = transfer.account_from " +
                "JOIN tenmo_user ON account.user_id = tenmo_user.user_id " +
                "WHERE tenmo_user.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            transferList.add(mapRowToTransfers(results));
        }
    return transferList;
    }

    // SQL tested via pgAdmin query: SUCCESS
    @Override
    public String getTransferDetails(int transferId) {
        Transfer transfer;
        String sql = "SELECT transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        transfer = mapRowToTransfers(results);
        return transfer.toString();
    }

    // SQL tested via pgAdmin query: SUCCESS
    @Override
    public boolean initiateTransfer(int senderId, int recipientId, BigDecimal amount) {
        boolean success = false;
        String sql = "START TRANSACTION; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE user_id = ?; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE user_id = ?; " +
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, (SELECT account_id FROM account WHERE user_id = ?), (SELECT account_id FROM account WHERE user_id = ?), ?); " +
                "COMMIT;";
        try {
            jdbcTemplate.update(sql, amount, senderId, amount, recipientId, senderId, recipientId, amount);
            success = true;
        } catch (DataIntegrityViolationException e) {
            sql = "ROLLBACK; ";
            jdbcTemplate.update(sql);
            success = false;
        }
        return success;
    }

    // SQL tested via pgAdmin query: SUCCESS
    @Override
    public int getUserId(String userName) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username = ?;";
        int userId = jdbcTemplate.queryForObject(sql, Integer.class, userName);
        return userId;
    }

    // TODO do I need these?
    public int getMaxId() {
        List<Transfer> transferIds = new ArrayList<>();
        String transfers = "SELECT transfer_id FROM transfer;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(transfers);
        while (results.next()) {
            transferIds.add(mapRowToTransfers(results));
        }
        int maxId = 3001;
        for (Transfer transfer : transferIds) {
            if (transfer.getTransferId() > maxId) {
                maxId = transfer.getTransferId();
            }
        }
        return maxId;
    }

    public int getMaxIdPlusOne() {
        return getMaxId() + 1;
    }


    private Transfer mapRowToTransfers(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        transfer.setUserFrom(rowSet.getInt("user_from"));
        transfer.setUserTo(rowSet.getInt("user_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }
}
