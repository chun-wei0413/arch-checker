package com.example.order;

import com.example.customer.Customer;

// Order aggregate root.
//
// VIOLATES R-DEP-01: in DDD an aggregate should not hold a hard reference
// to another aggregate; it should only carry the foreign aggregate's id
// (here: customerId: long) and look the other aggregate up via a service
// when needed. Holding a Customer field couples Order's lifecycle to
// Customer's, complicates loading, and crosses transaction boundaries.
public final class Order {

    private final long id;
    private final Customer customer;
    private final long totalCents;

    public Order(long id, Customer customer, long totalCents) {
        this.id = id;
        this.customer = customer;
        this.totalCents = totalCents;
    }

    public long id() {
        return id;
    }

    public Customer customer() {
        return customer;
    }

    public long totalCents() {
        return totalCents;
    }
}
