package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransferDao {

    public List<Transfer> listTransfersByUser(String user);

    public String getTransferDetails(int transferId);

    public boolean sendMoney(Transfer transfer);

    public int getMaxId();

    public int getMaxIdPlusOne();

    // Request money (optional)

}
