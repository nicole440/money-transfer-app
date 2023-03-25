package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {

    public BigDecimal getBalanceByUser(String user);

}
