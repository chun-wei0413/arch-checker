package com.example.service;

/**
 * Books a customer order. Complies with the Style Profile —
 * the type ends with the suffix "Service".
 */
public class OrderService {

    public String book(String customerId, String sku) {
        return "ORDER-" + customerId + "-" + sku;
    }
}
