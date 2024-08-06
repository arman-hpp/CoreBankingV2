package com.bank.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.bank.services.customers.CustomerService;

@Controller
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService _customerService;

    public CustomerController(CustomerService customerService) {
        _customerService = customerService;
    }


}