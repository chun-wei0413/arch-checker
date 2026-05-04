package com.example.controller;

import com.example.support.BaseController;

// HomeController complies with all four rules:
//   R-NAME-01  ends with "Controller"
//   R-SUP-01   extends BaseController
public class HomeController extends BaseController {

    public String home() {
        return "home";
    }
}
