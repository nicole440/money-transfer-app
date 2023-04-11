package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class Transfer {

    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int userFrom;
    private int userTo;
    @DecimalMin("1.00")
    private BigDecimal amount;

    public Transfer() {}

    public Transfer(int transferId, int transferTypeId, int transferStatusId, int userFrom, int userTo, BigDecimal amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.amount = amount;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(int userFrom) {
        this.userFrom = userFrom;
    }

    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // TODO determine whether this is needed
    @Override
    public String toString() {
        return "Transfer ID: " + getTransferId() +
                " | Transfer Type ID: " + getTransferTypeId() +
                " | Transfer From User: " + getUserFrom() +
                " | Transfer To User: " + getUserTo() +
                " | Amount: " + getAmount() +
                " | Transfer Status: " + getTransferStatusId();

    }
}
