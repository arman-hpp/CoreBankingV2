package com.bank.services.customers;

import com.bank.dtos.PagedResponseDto;
import com.bank.dtos.filters.FilterInfoDto;
import com.bank.dtos.customers.CustomerDto;
import com.bank.exceptions.BusinessException;
import com.bank.models.filters.BaseFilter;
import com.bank.models.filters.FilterSpecification;
import com.bank.models.customers.Customer;
import com.bank.repos.customers.CustomerRepository;
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
    private final CustomerRepository _customerRepository;
    private final ModelMapper _modelMapper;

    public CustomerService(CustomerRepository customerRepository, ModelMapper modelMapper) {
        _customerRepository = customerRepository;
        _modelMapper = modelMapper;
    }

    @Cacheable(value = "customers", key="'customers-page-'+#page + '-' + #size")
    public PagedResponseDto<CustomerDto> loadCustomers(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var customers = _customerRepository.findAll(pageable);
        var results = customers.map(customer -> _modelMapper.map(customer, CustomerDto.class));
        return new PagedResponseDto<>(results);
    }

    @Cacheable(value = "customer", key = "#customerId")
    public CustomerDto loadCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new BusinessException("error.customer.notFound");

        return _modelMapper.map(customer, CustomerDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "customers", allEntries = true)
    }, put = {
            @CachePut(cacheNames = "customer", key = "#customerDto.id")
    })
    public CustomerDto addOrEditCustomer(CustomerDto customerDto) {
        if (customerDto.getId() == null || customerDto.getId() <= 0) {
            return addCustomer(customerDto);
        } else {
            return editCustomer(customerDto);
        }
    }

    private CustomerDto addCustomer(CustomerDto customerDto) {
        var customer = _modelMapper.map(customerDto, Customer.class);
        _customerRepository.save(customer);

        customerDto.setId(customer.getId());
        return customerDto;
    }

    private CustomerDto editCustomer(CustomerDto customerDto) {
        var customer = _customerRepository.findById(customerDto.getId()).orElse(null);
        if (customer == null)
            throw new BusinessException("error.customer.notFound");

        _modelMapper.map(customerDto, customer);
        _customerRepository.save(customer);

        return customerDto;
    }

    @Caching(evict = {
            @CacheEvict(value = "customers", allEntries = true),
            @CacheEvict(cacheNames = "customer", key = "#customerId")
    })
    public void removeCustomer(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new BusinessException("error.customer.notFound");

        try {
            _customerRepository.delete(customer);
        }
        catch (DataIntegrityViolationException ex) {
            throw new BusinessException("error.public.dependent.entity");
        }
    }

    public PagedResponseDto<CustomerDto> loadCustomerByFilter(FilterInfoDto filterInfo) {
        var pageable = PageRequest.of(
                filterInfo.getFilterPage().getPage(),
                filterInfo.getFilterPage().getSize(),
                Sort.by(filterInfo.getFilterSort().getDirection(),
                        filterInfo.getFilterSort().getProps())
        );
        var filtersList = filterInfo.getFilters().stream()
                .map(f -> _modelMapper.map(f, BaseFilter.class))
                .toList();
        var spec = new FilterSpecification<Customer>(filtersList);
        var customers = _customerRepository.findAll(spec, pageable);
        var results = customers.map(customer -> _modelMapper.map(customer, CustomerDto.class));
        return new PagedResponseDto<>(results);
    }
}
