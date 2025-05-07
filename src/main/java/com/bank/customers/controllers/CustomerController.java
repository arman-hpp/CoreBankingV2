package com.bank.customers.controllers;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.bank.customers.dtos.AddCustomerRequestDto;
import com.bank.customers.dtos.CustomerResponseDto;
import com.bank.customers.dtos.EditCustomerRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.bank.customers.services.CustomerService;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "APIs for managing customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Returns a paginated list of customers")
    public PagedResponseDto<CustomerResponseDto> getAllCustomers(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") final Integer page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") final Integer size) {
        return customerService.loadCustomers(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Returns a single customer by ID")
    public CustomerResponseDto getCustomerById(
            @Parameter(description = "ID of the customer", example = "1")
            @PathVariable final Long id) {
        return customerService.loadCustomer(id);
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter customers", description = "Retrieve customers based on filter criteria")
    public PagedResponseDto<CustomerResponseDto> filterCustomers(
            @RequestBody final FilterInfoDto filterInfo) {
        return customerService.loadCustomerByFilter(filterInfo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new customer", description = "Create a new customer with the provided data")
    public CustomerResponseDto addCustomer(
            @RequestBody final AddCustomerRequestDto customerDto) {
        return customerService.addCustomer(customerDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit an existing customer", description = "Update customer data based on provided ID and new values")
    public CustomerResponseDto editCustomer(
            @Parameter(description = "ID of the customer to update", example = "1")
            @PathVariable final Long id,
            @RequestBody final EditCustomerRequestDto customerDto) {
        customerDto.setId(id);
        return customerService.editCustomer(customerDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Remove a customer by their ID")
    public void deleteCustomer(
            @Parameter(description = "ID of the customer to delete", example = "1")
            @PathVariable final Long id) {
        customerService.removeCustomer(id);
    }
}