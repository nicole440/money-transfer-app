package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.LoginDto;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler methods to route user info requests
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("users")
public class UserController {

    @Autowired
    private TransferDao transferDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;

    public UserController(TransferDao transferDao, UserDao userDao, AccountDao accountDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public List<User> getAuthenticatedUsers() {
        List<User> authenticatedUsers = new ArrayList<>();
        for (User user : userDao.findAll()) {
            authenticatedUsers.add(user);
        } return authenticatedUsers;
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public User getAuthenticatedUserById(@PathVariable int id) {
        return userDao.getUserById(id);
    }

    @RequestMapping(path = "/profile/{username}", method = RequestMethod.GET)
    public User getUserByUserName(@RequestParam(defaultValue = "") String user_like) {
        User returnedUser = new User();
        if (!user_like.equals("")) {
            returnedUser = userDao.findByUsername(user_like);
        } return returnedUser;
    }

//    @RequestMapping(method = RequestMethod.GET)
//    public int getUserIdByUserName(@RequestParam(defaultValue = "") String user_like) {
//        int retrievedUserId = 0;
//        if (!user_like.equals("")) {
//            retrievedUserId = userDao.findIdByUsername(user_like);
//        } return retrievedUserId;
//    }
}
