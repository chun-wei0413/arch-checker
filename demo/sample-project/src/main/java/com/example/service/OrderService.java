package com.example.service;

// Books a customer order. Complies with every rule in demo-profile.yaml:
//   R-NAME-01  ends with the "Service" suffix
//   R-DEP-01   imports nothing from com.example.controller
//   R-SUP-01   not in com.example.controller, so the rule does not apply
public class OrderService {

    public String book(String customerId, String sku) {
        return "ORDER-" + customerId + "-" + sku;
    }
}
