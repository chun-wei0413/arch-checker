package com.example.service;

// Legacy entry point — predates the *Service naming rule.
// Will be flagged by NamingRule R-NAME-01 and then accepted
// via UC-04 (suppress) for demonstration purposes.

public class UserManager {

    public String whoami() {
        return "legacy-user";
    }
}
