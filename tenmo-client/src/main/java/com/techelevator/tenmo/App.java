package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;
import java.math.BigDecimal;

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
        consoleService.printBalance(accountService.getBalance());
    }

    private void viewTransferHistory() {
 //     int userId = currentUser.getUser().getId();
        Transfer[] transferArray = transferService.getTransferHistory();
        consoleService.printTransferHistory(transferArray);
        int transferId = consoleService.selectTransfer(transferArray);
        if (transferId != -1) {
            Transfer singleTransfer = transferService.getTransferDetailsByTransferId(transferId);
            consoleService.printSingleTransfer(singleTransfer);
        }
    }

    // SEND MONEY METHODS

    private void sendBucks() {
        User[] users = userService.getAuthenticatedUsersArray();
        consoleService.printUsers(users);
        int recipient = consoleService.promptForInt("\nEnter the ID of the user to receive funds: ");
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount (as a decimal) that you'd like to transfer: ");
        Transfer transfer = new Transfer(recipient, transferAmount);
        transfer.setUserTo(recipient);
        transfer.setAmount(transferAmount);
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            consoleService.printErrorMessage(); // TODO make a new message that's more descriptive
        }
        transfer.setUserFrom(currentUser.getUser().getId());
        if (isValidRecipient(transfer) && senderHasSufficientFunds(transfer)) {
            transferService.sendMoney(transfer);
            consoleService.sendSuccess();
        } else {
            consoleService.sendUnsuccessful();
        }
    }

    private boolean isValidRecipient(Transfer transfer) {
        if (transfer.getUserFrom() == transfer.getUserTo()) {
            consoleService.printCantSendToSelf();
            return false;
        }
        return true;
    }

    private boolean senderHasSufficientFunds(Transfer transfer) {
        if (transfer.getAmount().compareTo(accountService.getBalance()) > 0) {
            consoleService.printInsufficientFunds();
            return false;
        }
        return true;
    }

    // REQUEST MONEY METHODS (OPTIONAL)

    private void requestBucks() {
        User[] users = userService.getAuthenticatedUsersArray();
        consoleService.printUsers(users);
        int senderId = consoleService.promptForInt("\nEnter the ID of the user from whom you wish to funds: ");
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Please enter the amount (as a decimal) that you'd like to request: ");
        Transfer transfer = new Transfer(senderId, transferAmount);
        transfer.setUserTo(currentUser.getUser().getId());
        transfer.setAmount(transferAmount);
        transfer.setUserFrom(senderId);
        if (isValidRecipient(transfer)) {
            transferService.requestMoney(transfer);
            // TODO add call to console service requesting success or failure message
        } else {

        }
    }

    private void viewPendingRequests() {
        // TODO Add functionality to view pending requests
    }
}
