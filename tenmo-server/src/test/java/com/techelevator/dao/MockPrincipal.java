package com.techelevator.dao;

import javax.security.auth.Subject;
import java.security.Principal;

public class MockPrincipal implements Principal {
    private String name;

    public MockPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }
}
