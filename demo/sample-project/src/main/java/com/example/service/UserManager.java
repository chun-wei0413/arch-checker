package com.example.service;

// Legacy entry point predating the *Service naming convention.
// Will be flagged by R-NAME-01 and then accepted via UC-04 (suppress)
// for demonstration purposes.

public class UserManager {

    public String whoami() {
        return "legacy-user";
    }
}
