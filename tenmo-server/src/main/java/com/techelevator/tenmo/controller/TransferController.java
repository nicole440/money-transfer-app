package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Handler methods to route transfer requests
 * Refer to userIds in path (instead of accountIds)
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("transfers")
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

    @RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> getTransfersByUserId(Principal principal) {
        String user = principal.getName();
        return transferDao.listTransfersByUser(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/newtransfer/", method = RequestMethod.POST)
    public void createTransfer(Transfer transfer) {
        transferDao.sendMoney(transfer);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public String getTransferDetailsById(@PathVariable int transferId) {
        return transferDao.getTransferDetails(transferId);
    }

    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public int getTransferId(@PathVariable int transferId) {
        return transferDao.getMaxIdPlusOne();
    }
}
