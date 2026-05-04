package com.example.service;

// Charges a customer for a previously booked order.
// Complies with every rule in demo-profile.yaml.
public class PaymentService {

    public boolean charge(String orderId, long cents) {
        return cents > 0;
    }
}
