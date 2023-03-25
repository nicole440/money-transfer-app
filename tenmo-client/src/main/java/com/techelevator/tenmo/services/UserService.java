package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class UserService {

    public static final String API_BASE_URL = "http://localhost:8080/";
    RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public User[] getAuthenticatedUsersArray() {
        User[] authenticatedUsers = null;
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(API_BASE_URL + "users/all", HttpMethod.GET, makeAuthEntity(), User[].class);
            authenticatedUsers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return authenticatedUsers;
    }

    public User getAuthenticatedUserById(int userId) {
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "users/" + userId, HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public User getUserByUserName(String userName) {
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange(API_BASE_URL + "users/" + userName, HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public Integer getUserIdByUserName(String userName) {
        Integer userId = 0;
        try {
            ResponseEntity<Integer> response = restTemplate.exchange(API_BASE_URL + "users/" + userName, HttpMethod.GET, makeAuthEntity(), Integer.class);
            userId = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return userId;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
