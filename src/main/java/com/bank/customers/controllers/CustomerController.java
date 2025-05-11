package com.bank.customers.controllers;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.core.dtos.PaginationRequestDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.bank.customers.dtos.AddCustomerRequestDto;
import com.bank.customers.dtos.CustomerResponseDto;
import com.bank.customers.dtos.EditCustomerRequestDto;
import com.bank.customers.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customers", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Retrieve all customers", description = "Fetches a paginated list of customers")
    public PagedResponseDto<CustomerResponseDto> getAll(
            @Valid @ModelAttribute PaginationRequestDto pagination) {
        return customerService.loadCustomers(pagination);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve customer by ID", description = "Fetches a single customer by their ID")
    public CustomerResponseDto getById(
            @PathVariable @Positive(message = "Customer ID must be positive")
            @Parameter(description = "ID of the customer", example = "1") Long id) {
        return customerService.loadCustomer(id);
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter customers", description = "Fetches customers based on provided filter criteria")
    public PagedResponseDto<CustomerResponseDto> filter(
            @Valid @RequestBody FilterInfoDto filterInfo) {
        return customerService.loadCustomerByFilter(filterInfo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details")
    public CustomerResponseDto create(
            @Valid @RequestBody AddCustomerRequestDto addCustomerRequest) {
        return customerService.addCustomer(addCustomerRequest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer", description = "Updates an existing customer by their ID")
    public CustomerResponseDto update(
            @PathVariable @Positive(message = "Customer ID must be positive")
            @Parameter(description = "ID of the customer to update", example = "1") Long id,
            @Valid @RequestBody EditCustomerRequestDto editCustomerRequest) {
        return customerService.editCustomer(id, editCustomerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Deletes a customer by their ID")
    public void delete(
            @PathVariable @Positive(message = "Customer ID must be positive")
            @Parameter(description = "ID of the customer to delete", example = "1") Long id) {
        customerService.removeCustomer(id);
    }
}