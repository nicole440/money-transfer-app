package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080";
    RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Transfer[] getTransferHistory() {
        Transfer[] transferArray = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "/transfers/history", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transferArray = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferArray;
    }

    // TODO fix transfer details methods - Null Pointer Exception
    public Transfer getTransferDetailsByTransferId(int transferId) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/transfers/history/" + transferId, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public boolean sendMoney(Transfer transfer) {
        boolean confirmation = false;
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(API_BASE_URL + "/transfers/send", HttpMethod.POST, entity, Boolean.class);
            confirmation = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return confirmation;
    }

    public boolean requestMoney(Transfer transfer) {
        boolean confirmation = false;
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(API_BASE_URL + "/transfers/request", HttpMethod.POST, entity, Boolean.class);
            confirmation = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return confirmation;
    }

    // TODO add functionality to approve or reject transaction request


    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
