package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transfer> listTransfersByUser(String user) {
        List<Transfer> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_to OR account.account_id = transfer.account_from " +
                "JOIN tenmo_user ON account.user_id = tenmo_user.user_id " +
                "WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user);
        while (results.next()) {
                transferList.add(mapRowToTransfers(results));
        }
    return transferList;
    }

    @Override
    public String getTransferDetails(int transferId) {
        Transfer transfer;
        String sql = "SELECT transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        transfer = mapRowToTransfers(results);
        return transfer.toString();
    }

    @Override
    public boolean sendMoney(Transfer transfer) {
        boolean success = false;
        String sql = "Start Transaction; " +
                "UPDATE account SET balance = balance - ? " +
                "WHERE account_id = ?; " +
                "UPDATE account SET balance = balance + ? " +
                "WHERE account_id = ?; " +
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, ?, ?, ?);" +
                "COMMIT;";
        // can't be to themselves
        if (transfer.getAccountFrom() != transfer.getAccountTo()) {
            // must be positive amount, can't be more than sender has
            //if (transfer.getAmount().compareTo(transfer.getAccountFrom().getBalance()) < 0 && (transfer.getAmount().compareTo(ZERO_BALANCE) >= 0)) {
                try {
                    jdbcTemplate.update(sql, transfer.getAmount());
                    success = true;
                } catch (DataIntegrityViolationException e) {
                    sql = "ROLLBACK; ";
                    jdbcTemplate.update(sql);
                    success = false;
                }
          //  }
        } return success;
    }

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
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }
}
