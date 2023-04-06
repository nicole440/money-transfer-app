package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Handler methods to route transfer requests
 * Refer to userIds in path (instead of accountIds)
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    private TransferDao transferDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;

    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransactionHistory(int userId) {
        return transferDao.listTransfersByUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/new", method = RequestMethod.POST)
    public boolean createTransfer(Transfer transfer, Principal principal) {
        int userId = transferDao.getUserId(principal.getName());
        boolean moneySent = transferDao.initiateTransfer(userId, transfer.getUserTo(), transfer.getAmount());
        if (moneySent == false) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "asdfkjkl;");
        }
        return moneySent;
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.GET)
    public String getTransferDetailsById(@PathVariable int transferId) {
        return transferDao.getTransferDetails(transferId);
    }

    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public int getTransferId(@PathVariable int transferId) {
        return transferDao.getMaxIdPlusOne();
    }
}
