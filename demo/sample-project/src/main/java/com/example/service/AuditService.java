package com.example.service;

import com.example.controller.HomeController;

// VIOLATES R-DEP-01: a service must not depend on a controller.
// Layering convention: Controller calls Service, never the reverse —
// otherwise the dependency graph cycles and the service becomes
// untestable without the web layer wired up.
public final class AuditService {

    public String wire(HomeController controller) {
        return "wired";
    }
}
