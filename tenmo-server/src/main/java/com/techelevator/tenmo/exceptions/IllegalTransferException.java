package com.techelevator.tenmo.exceptions;

public class IllegalTransferException extends Exception {
    public IllegalTransferException(String message) {
        super("Illegal transfer.");
    }
}
