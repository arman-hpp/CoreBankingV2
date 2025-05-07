package com.bank.customers.controllers;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.bank.customers.dtos.AddCustomerRequestDto;
import com.bank.customers.dtos.CustomerResponseDto;
import com.bank.customers.dtos.EditCustomerRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.bank.customers.services.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public PagedResponseDto<CustomerResponseDto> getAllCustomers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return customerService.loadCustomers(page, size);
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getCustomerById(@PathVariable Long id) {
        return customerService.loadCustomer(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDto addCustomer(@RequestBody AddCustomerRequestDto customerDto) {
        return customerService.addCustomer(customerDto);
    }

    @PutMapping("/{id}")
    public CustomerResponseDto editCustomer(@PathVariable Long id, @RequestBody EditCustomerRequestDto customerDto) {
        customerDto.setId(id);
        return customerService.editCustomer(customerDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.removeCustomer(id);
    }

    @PostMapping("/filter")
    public PagedResponseDto<CustomerResponseDto> filterCustomers(@RequestBody FilterInfoDto filterInfo) {
        return customerService.loadCustomerByFilter(filterInfo);
    }
}