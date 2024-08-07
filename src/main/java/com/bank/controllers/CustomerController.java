package com.bank.controllers;

import com.bank.dtos.filters.FilterInfoDto;
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
    public Page<CustomerDto> getAllCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _customerService.loadCustomers(page, size);
    }

    @GetMapping("/getCustomerById/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        return _customerService.loadCustomer(id);
    }

    @PostMapping("/addOrEditCustomer")
    public CustomerDto addOrEditCustomer(@RequestBody CustomerDto customerDto) {
        return _customerService.addOrEditCustomer(customerDto);
    }

    @DeleteMapping("/addOrEditCustomer/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        _customerService.removeCustomer(id);
    }

    @PostMapping("/filterCustomers")
    public Page<CustomerDto> filterCustomers(@RequestBody FilterInfoDto filterInfo) {
        return _customerService.loadCustomerByFilter(filterInfo);
    }
}