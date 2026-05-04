package com.example.domain.payment;

// VIOLATES R-SUP-01: every type in com.example.domain.payment must
// extend the abstract Payment base type so that the rest of the system
// can treat all payments polymorphically (a List<Payment> covering
// every concrete kind).
//
// Skipping the base type means CashPayment cannot be stored in the
// same collection as CreditCardPayment / BankTransferPayment, and
// PaymentService would need a special branch for it — exactly the
// duplication the base type exists to prevent.
public final class CashPayment {

    private final long cents;

    public CashPayment(long cents) {
        this.cents = cents;
    }

    public long cents() {
        return cents;
    }

    public String method() {
        return "cash";
    }
}
