package com.example.order;

// Application service for the Order aggregate.
// Compliant with every rule in demo-profile.yaml.
public final class OrderService {

    public Order place(long customerId, long totalCents) {
        // In a real system the customer would be resolved by id via a
        // CustomerService here, then an Order would be created.
        return null;
    }
}
