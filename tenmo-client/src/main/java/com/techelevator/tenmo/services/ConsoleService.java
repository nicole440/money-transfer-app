package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void printBalance(BigDecimal balance) {
        System.out.println("You have $" + balance + " in your account.");
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void sendSuccess() {
        System.out.println("Your transfer has been initiated.");
    }

    public void sendUnsuccessful() {
        System.out.println("Transfer failed. Please try again later.");
    }

    // TODO Add print statements reflecting success or failure of transfer requests

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printCantSendToSelf() {
        System.out.println("You cannot transfer money to yourself.");
    }

    public void printInsufficientFunds() {
        System.out.println("Insufficient funds. Unable to complete transfer.");
    }

    public void printUsers(User[] users) {
        System.out.println("--------------------------------------------");
        System.out.println("                TEnmo Users");
        System.out.println("--------------------------------------------");
        if (users != null) {
            for (User user : users) {
                System.out.println(user.userToString());
            }
        }
    }

    public void printTransferHistory(Transfer[] transfers) {
        System.out.println("------------------------------------------------------");
        System.out.println("                   TRANSFER HISTORY");
        System.out.println("------------------------------------------------------");
        printHistory(transfers);
    }

    public void printHistory(Transfer[] transferArray) {
        System.out.println("Transfer ID\t\tStatus\t\tFrom/To\t\t\tAmount\n");
        for (Transfer transfer : transferArray) {
            System.out.print("\t" + transfer.getTransferId() + "\t\t");
            System.out.print(transfer.getTransferStatusId() + "\t\t");
            System.out.print("From: " + transfer.getUserFrom());
            System.out.print(" To: " + transfer.getUserTo()); // TODO fix print out of acct info // Get user name?
            System.out.print("     $" + transfer.getAmount() + "\n");
        }
    }

    public int selectTransfer(Transfer[] transfers) {
        int transferId = promptForInt("\nPlease enter a transfer ID for more details: ");
        for (Transfer transfer : transfers) {
            if (transferId == transfer.getTransferId()) {
                return transferId;
            }
        }
        System.out.println("Transfer ID not found.  Please enter a correct transfer ID. ");
        return -1;
    }

    // TODO fix null pointer
    public void printSingleTransfer(Transfer transfer) {
        System.out.println("TRANSFER DETAILS\nTransfer ID: " + transfer.getTransferId() + "\nAmount : $" + transfer.getAmount() + "\nFrom: " + transfer.getUserFrom() + "\nTo: " + transfer.getUserTo() + "\nStatus: " + transfer.getTransferStatusId() + "\nType: " + transfer.getTransferTypeId());
    }

}
