package com.example.domain.payment;

import com.example.domain.core.Payment;

// Compliant — extends Payment so it participates in the polymorphic
// payment domain model.
public final class CreditCardPayment extends Payment {

    private final String last4;

    public CreditCardPayment(long cents, String last4) {
        super(cents);
        this.last4 = last4;
    }

    public String last4() {
        return last4;
    }

    @Override
    public String method() {
        return "credit-card";
    }
}
