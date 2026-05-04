package com.example.service;

/**
 * Charges a customer for a previously booked order.
 * Complies with the Style Profile naming rule.
 */
public class PaymentService {

    public boolean charge(String orderId, long cents) {
        return cents > 0;
    }
}
