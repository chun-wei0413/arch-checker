package com.example.service;

// Books a customer order. Compliant with every rule in demo-profile.yaml.
public final class OrderService {

    public String book(long customerId, String sku) {
        return "ORDER-" + customerId + "-" + sku;
    }
}
