package com.example.service;

import com.example.controller.HomeController;

// AuditService violates R-DEP-01: a service must not depend on the controller layer.
// Its own name still complies with R-NAME-01 (ends with "Service").
public class AuditService {

    public String wire(HomeController controller) {
        return "wired";
    }
}
