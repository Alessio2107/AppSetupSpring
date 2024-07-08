package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomerHomeController {
    @GetMapping("/customer/home")
    public String customerHome(){
        return "customerHome";
    }
}
