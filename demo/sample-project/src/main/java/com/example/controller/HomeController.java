package com.example.controller;

import com.example.support.BaseController;

// Compliant with every rule:
//   R-NAME-01  ends with "Controller"
//   R-SUP-01   extends BaseController
public final class HomeController extends BaseController {

    public String index() {
        return render("home");
    }
}
