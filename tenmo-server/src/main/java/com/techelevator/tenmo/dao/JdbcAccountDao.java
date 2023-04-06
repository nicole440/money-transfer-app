package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // SQL tested via pgAdmin query: SUCCESS
    @Override
    public BigDecimal getBalanceByUser(String user) throws IllegalArgumentException {
        BigDecimal balance = BigDecimal.valueOf(0.00);
        String sql = "SELECT balance FROM account " +
                "JOIN tenmo_user ON account.user_id = tenmo_user.user_id " +
                "WHERE username = ?;";
        try {
            balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        return balance;
    }
}
