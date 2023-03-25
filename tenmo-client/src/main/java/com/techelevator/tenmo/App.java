package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.Arrays;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private AuthenticatedUser currentUser;
    private TransferService transferService = new TransferService();
    private UserService userService = new UserService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser != null) {
            transferService.setAuthToken(currentUser.getToken());
            accountService.setAuthToken(currentUser.getToken(), currentUser);
            userService.setAuthToken(currentUser.getToken());
        } else consoleService.printErrorMessage();
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        // Call to AccountService
        System.out.println("Your current account balance is: $" + accountService.getBalance() + ".");
    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub
        // Call to TransferService
        System.out.println(Arrays.toString(transferService.getAllTransfers()) + ".");
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        // Call to TransferService
    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        Transfer transfer = new Transfer();
        User[] users = userService.getAuthenticatedUsersArray();
        if (users != null) {
            transfer.setAccountFrom(currentUser.getUser().getId() + 1000); // I know this sucks, don't judge me.
            consoleService.printUsers(users);
            transfer.setAccountTo(consoleService.promptForInt("\nEnter the ID of the user to receive funds: ") + 1000);
            BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount (as a decimal) that you'd like to transfer: ");
            //if (transferAmount.compareTo(BigDecimal.ZERO) > 0 && (transferAmount.compareTo(accountService.getBalance()) > 0)) {
                transfer.setAmount(transferAmount);
                transfer.setTransferTypeId(2);
                transfer.setTransferStatusId(2);
            //    transfer.setTransferId(transferService.transferId(transfer.getTransferId()));
                transferService.createTransfer(transfer);
            }
    //    } else {
     //       consoleService.printErrorMessage();
    //    }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        // Call to TransferService
    }
}
