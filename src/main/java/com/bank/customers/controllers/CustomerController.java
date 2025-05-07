package com.bank.customers.controllers;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.bank.customers.dtos.AddCustomerRequestDto;
import com.bank.customers.dtos.CustomerResponseDto;
import com.bank.customers.dtos.EditCustomerRequestDto;
import org.springframework.web.bind.annotation.*;
import com.bank.customers.services.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService _customerService;

    public CustomerController(CustomerService customerService) {
        _customerService = customerService;
    }

    @GetMapping("/")
    public PagedResponseDto<CustomerResponseDto> getAllCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return _customerService.loadCustomers(page, size);
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getCustomerById(@PathVariable Long id) {
        return _customerService.loadCustomer(id);
    }

    @PostMapping("/")
    public CustomerResponseDto addCustomer(@RequestBody AddCustomerRequestDto customerDto) {
        return _customerService.addCustomer(customerDto);
    }

    @PostMapping("/")
    public CustomerResponseDto editCustomer(@RequestBody EditCustomerRequestDto customerDto) {
        return _customerService.editCustomer(customerDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        _customerService.removeCustomer(id);
    }

    @PostMapping("/filter")
    public PagedResponseDto<CustomerResponseDto> filterCustomers(@RequestBody FilterInfoDto filterInfo) {
        return _customerService.loadCustomerByFilter(filterInfo);
    }
}