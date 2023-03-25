package com.techelevator.tenmo.exceptions;

public class InsufficientFundsException extends Exception{
    public InsufficientFundsException(String message) {
        super("Insufficient funds.");
    }
}
