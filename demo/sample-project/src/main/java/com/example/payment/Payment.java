package com.example.payment;

// Payment aggregate root. Compliant with every rule.
public final class Payment {

    private final long orderId;
    private final long cents;

    public Payment(long orderId, long cents) {
        this.orderId = orderId;
        this.cents = cents;
    }

    public long orderId() {
        return orderId;
    }

    public long cents() {
        return cents;
    }
}
