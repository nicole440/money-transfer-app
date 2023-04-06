package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> listTransfersByUser(int userId);

    String getTransferDetails(int transferId);

    boolean initiateTransfer(int senderId, int recipientId, BigDecimal amount);

    int getUserId(String userName);

    int getMaxId();

    int getMaxIdPlusOne();

    // Request money (optional)

}
