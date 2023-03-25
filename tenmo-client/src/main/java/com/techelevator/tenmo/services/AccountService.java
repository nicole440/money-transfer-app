package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    AuthenticatedUser currentUser;
    public static String authToken;

    public void setAuthToken(String authToken, AuthenticatedUser currentUser) {
        this.authToken = authToken;
        this.currentUser = currentUser;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0.00);
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL + "accounts/balance/", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
