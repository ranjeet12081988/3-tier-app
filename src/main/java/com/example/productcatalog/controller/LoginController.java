package com.example.productcatalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // CHANGE THIS LINE: Redirect to /login instead of /products
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}