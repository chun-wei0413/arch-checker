package com.example.controller;

// LegacyController violates R-SUP-01: anything in com.example.controller
// must extend BaseController. The name itself complies with R-NAME-01.
public class LegacyController {

    public String legacy() {
        return "old";
    }
}
