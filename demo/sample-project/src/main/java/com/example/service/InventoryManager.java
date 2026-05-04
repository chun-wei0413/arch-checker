package com.example.service;

// VIOLATES R-NAME-01: the suffix "Manager" is a vague catch-all that does
// not communicate a concrete responsibility. Strict profile prefers
// role-specific suffixes such as Service, Coordinator, Reservation, or
// Allocator. Compare:
//
//   InventoryManager       — manages what? read? write? both?
//   InventoryReservation   — reserves stock for an order
//   InventoryAllocator     — allocates stock across warehouses
//   InventoryService       — generic application-layer entry point
public final class InventoryManager {

    public boolean reserve(long sku, int quantity) {
        return quantity > 0;
    }
}
