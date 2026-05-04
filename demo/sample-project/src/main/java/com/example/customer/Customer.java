package com.example.customer;

// Aggregate root for the Customer bounded context.
// Compliant with every rule in demo-profile.yaml.
public final class Customer {

    private final long id;
    private final String email;

    public Customer(long id, String email) {
        this.id = id;
        this.email = email;
    }

    public long id() {
        return id;
    }

    public String email() {
        return email;
    }
}
