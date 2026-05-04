package com.example.domain.core;

// Abstract base type for every concrete payment in the domain layer.
//
// Lives in com.example.domain.core (not com.example.domain.payment) so
// that SupertypeRule's targetPackage = com.example.domain.payment does
// not flag the base type itself for failing to extend itself.
public abstract class Payment {

    private final long cents;

    protected Payment(long cents) {
        this.cents = cents;
    }

    public long cents() {
        return cents;
    }

    public abstract String method();
}
