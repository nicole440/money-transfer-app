package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.IllegalTransferException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
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

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public List<Transfer> getTransactionHistory(Principal principal) {
        int currentUserID = userDao.findIdByUsername(principal.getName());
        List<Transfer> transferList = transferDao.listTransfersByUser(currentUserID);
        return transferList;
    }

    @RequestMapping(path = "/history/{transferId}", method = RequestMethod.GET)
    public Transfer getTransferDetailsById(@PathVariable int transferId, int userId) {
        return transferDao.getTransferDetails(transferId, userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/send", method = RequestMethod.POST)
    public boolean sendMoney(@RequestBody Transfer transfer, Principal principal) throws IllegalTransferException {
        int userId = transferDao.getUserId(principal.getName());
        boolean moneySent = transferDao.sendMoney(userId, transfer.getUserTo(), transfer.getAmount());
        if (moneySent == false) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transfer."); // TODO make response more descriptive
        }
        return moneySent;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public boolean requestMoney(@RequestBody Transfer transfer, Principal principal) {
        boolean transferRequested = false;
        int userId = transferDao.getUserId(principal.getName());
        BigDecimal amount = transfer.getAmount();
        try {
            transferDao.requestMoney(userId, transfer.getUserFrom(), amount);
            transferRequested = true;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transfer."); // TODO make response more descriptive
        }
        return transferRequested;
    }

    // TODO add functionality to approve or reject transaction request

}
