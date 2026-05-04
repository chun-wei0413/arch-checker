package com.example.service;

// VIOLATES R-NAME-01: the suffix "Manager" is a vague catch-all that
// does not say what the type actually does — refund? retry? settle?
// reconcile? Common code review feedback is to pick a role-specific
// suffix instead:
//
//   PaymentManager        — manages what?
//   PaymentService        — generic application-layer entry point
//   PaymentReconciler     — reconciles payments against bank statements
//   PaymentRetryPolicy    — policy object for retrying failed charges
public final class PaymentManager {

    public boolean reconcile(long paymentId) {
        return paymentId > 0;
    }
}
