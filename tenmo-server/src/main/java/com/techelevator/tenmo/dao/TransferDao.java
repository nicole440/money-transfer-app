package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> listTransfersByUser(int userId);

    Transfer getTransferDetails(int transferId, int userId);

    boolean sendMoney(int senderId, int recipientId, BigDecimal amount);

    void requestMoney(int recipientId, int senderId, BigDecimal amount);

    int getUserId(String userName);


}
