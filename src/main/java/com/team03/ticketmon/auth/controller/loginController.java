package com.team03.ticketmon.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class loginController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
