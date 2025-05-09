package com.bank.customers.services;

import com.bank.core.dtos.PagedResponseDto;
import com.bank.core.dtos.PaginationRequestDto;
import com.bank.core.dtos.filters.FilterInfoDto;
import com.bank.customers.dtos.AddCustomerRequestDto;
import com.bank.core.exceptions.BusinessException;
import com.bank.core.models.filters.BaseFilter;
import com.bank.core.models.filters.FilterSpecification;
import com.bank.customers.dtos.CustomerResponseDto;
import com.bank.customers.dtos.EditCustomerRequestDto;
import com.bank.customers.models.Customer;
import com.bank.customers.repos.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Cacheable(value = "customers", key="'customers-page-'+#paginationDto.pageNumber + '-' + #paginationDto.pageSize")
    public PagedResponseDto<CustomerResponseDto> loadCustomers(PaginationRequestDto paginationDto) {
        var pageable = PageRequest.of(paginationDto.getPageNumber(), paginationDto.getPageSize(), Sort.by(Sort.Direction.DESC, "Id"));
        var customers = customerRepository.findAll(pageable);
        var customerDtos = customers.map(customer -> modelMapper.map(customer, CustomerResponseDto.class));
        return new PagedResponseDto<>(customerDtos);
    }

    @Cacheable(value = "customer", key = "#customerId")
    public CustomerResponseDto loadCustomer(Long customerId) {
        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new BusinessException("error.customer.notFound");

        return modelMapper.map(customer, CustomerResponseDto.class);
    }

    @CacheEvict(value = "customers", allEntries = true)
    public CustomerResponseDto addCustomer(AddCustomerRequestDto customerDto) {
        var customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerResponseDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "customers", allEntries = true)
    }, put = {
            @CachePut(cacheNames = "customer", key = "#customerId")
    })
    public CustomerResponseDto editCustomer(Long customerId, EditCustomerRequestDto customerDto) {
        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new BusinessException("error.customer.notFound");

        modelMapper.map(customerDto, customer);
        customerRepository.save(customer);

        return modelMapper.map(customer, CustomerResponseDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "customers", allEntries = true),
            @CacheEvict(cacheNames = "customer", key = "#customerId")
    })
    public void removeCustomer(Long customerId) {
        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new BusinessException("error.customer.notFound");

        try {
            customerRepository.delete(customer);
        }
        catch (DataIntegrityViolationException ex) {
            throw new BusinessException("error.public.dependent.entity");
        }
    }

    public PagedResponseDto<CustomerResponseDto> loadCustomerByFilter(FilterInfoDto filterInfo) {
        var pageable = PageRequest.of(
                filterInfo.getFilterPage().getPage(),
                filterInfo.getFilterPage().getSize(),
                Sort.by(filterInfo.getFilterSort().getDirection(),
                        filterInfo.getFilterSort().getProps())
        );
        var filtersList = filterInfo.getFilters().stream()
                .map(f -> modelMapper.map(f, BaseFilter.class))
                .toList();
        var spec = new FilterSpecification<Customer>(filtersList);
        var customers = customerRepository.findAll(spec, pageable);
        var results = customers.map(customer -> modelMapper.map(customer, CustomerResponseDto.class));
        return new PagedResponseDto<>(results);
    }
}
