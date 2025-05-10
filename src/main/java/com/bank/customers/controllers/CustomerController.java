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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Validated
@Tag(name = "Customers", description = "APIs for managing customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Returns a paginated list of customers")
    public ResponseEntity<PagedResponseDto<CustomerResponseDto>> getAllCustomers(
            @Valid @ModelAttribute PaginationRequestDto paginationDto) {
        return ResponseEntity.ok(customerService.loadCustomers(paginationDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Returns a single customer by ID")
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @Positive(message = "Customer ID must be positive")
            @Parameter(description = "ID of the customer", example = "1")
            @PathVariable final Long id) {
        return ResponseEntity.ok(customerService.loadCustomer(id));
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter customers", description = "Retrieve customers based on filter criteria")
    public ResponseEntity<PagedResponseDto<CustomerResponseDto>> filterCustomers(
            @Valid @RequestBody final FilterInfoDto filterInfo) {
        return ResponseEntity.ok(customerService.loadCustomerByFilter(filterInfo));
    }

    @PostMapping
    @Operation(summary = "Add a new customer", description = "Create a new customer with the provided data")
    public ResponseEntity<CustomerResponseDto> addCustomer(
            @Valid @RequestBody final AddCustomerRequestDto addCustomerDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.addCustomer(addCustomerDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit an existing customer", description = "Update customer data based on provided ID and new values")
    public ResponseEntity<CustomerResponseDto> editCustomer(
            @Positive(message = "Customer ID must be positive")
            @Parameter(description = "ID of the customer to update", example = "1")
            @PathVariable final Long id,
            @Valid @RequestBody final EditCustomerRequestDto editCustomerDto) {
        return ResponseEntity.ok(customerService.editCustomer(id, editCustomerDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Remove a customer by their ID")
    public ResponseEntity<Void> deleteCustomer(
            @Positive(message = "Customer ID must be positive")
            @Parameter(description = "ID of the customer to delete", example = "1")
            @PathVariable final Long id) {
        customerService.removeCustomer(id);
        return ResponseEntity.noContent().build();
    }
}