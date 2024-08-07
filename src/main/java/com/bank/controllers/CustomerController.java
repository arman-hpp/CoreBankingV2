package com.bank.controllers;

import com.bank.dtos.customers.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.bank.services.customers.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService _customerService;

    public CustomerController(CustomerService customerService) {
        _customerService = customerService;
    }

    @GetMapping("/getAllCustomers")
    public Page<CustomerDto> getAllCustomers() {
        return _customerService.loadCustomers(0, 10);
    }
}