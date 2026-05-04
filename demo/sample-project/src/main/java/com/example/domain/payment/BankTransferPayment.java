package com.example.domain.payment;

import com.example.domain.core.Payment;

// Compliant — extends Payment.
public final class BankTransferPayment extends Payment {

    private final String iban;

    public BankTransferPayment(long cents, String iban) {
        super(cents);
        this.iban = iban;
    }

    public String iban() {
        return iban;
    }

    @Override
    public String method() {
        return "bank-transfer";
    }
}
