package com.example.controller;

import com.example.domain.core.Payment;
import com.example.service.PaymentService;

// Compliant. Pure presentation-layer controller that delegates to
// PaymentService. Intentionally simple — controllers are not the focus
// of this demo.
public final class PaymentController {

    private final PaymentService service = new PaymentService();

    public String charge(Payment payment) {
        return service.charge(payment) ? "OK" : "DECLINED";
    }
}
