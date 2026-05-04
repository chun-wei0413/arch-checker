package com.example.service;

// VIOLATES R-NAME-01: the suffix "Manager" is a vague catch-all that does
// not say what the type actually does — read? write? both? Common code
// review feedback is to pick a role-specific suffix instead:
//
//   InventoryManager       — manages what?
//   InventoryService       — generic application-layer entry point
//   InventoryReservation   — reserves stock for an order
//   InventoryAllocator     — allocates stock across warehouses
public final class InventoryManager {

    public boolean reserve(long sku, int quantity) {
        return quantity > 0;
    }
}
