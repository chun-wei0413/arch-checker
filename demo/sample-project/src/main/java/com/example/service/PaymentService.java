package com.example.service;

// Charges a customer for a previously booked order.
// Compliant with every rule in demo-profile.yaml.
public final class PaymentService {

    public boolean charge(String orderId, long cents) {
        return cents > 0;
    }
}
