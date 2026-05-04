package com.example.support;

// Shared base type used by everything in com.example.controller.
//
// Lives in com.example.support (not com.example.controller) so that
// SupertypeRule's targetPackage = com.example.controller does not
// flag the base type itself for failing to extend itself.
public abstract class BaseController {

    protected final String render(String view) {
        return "VIEW: " + view;
    }
}
