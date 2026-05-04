package com.example.service;

import com.example.domain.core.Payment;

// Application service for charging a payment. Compliant with every rule.
public final class PaymentService {

    public boolean charge(Payment payment) {
        return payment.cents() > 0;
    }
}
