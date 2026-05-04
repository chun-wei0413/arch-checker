package com.example.controller;

// VIOLATES R-SUP-01: every type in com.example.controller must extend
// BaseController. Extending the shared base type lets all controllers
// inherit common error handling, logging, and authentication middleware.
// Skipping it means CartController quietly diverges from the rest of the
// presentation layer.
public final class CartController {

    public String show(long cartId) {
        return "cart-" + cartId;
    }
}
