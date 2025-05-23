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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CustomerService {
    private static final String CUSTOMER_PAGES_CACHE = "customer-pages";
    private static final String CUSTOMER_BY_ID_CACHE = "customer-by-id";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = CUSTOMER_PAGES_CACHE)
    public PagedResponseDto<CustomerResponseDto> getAll(PaginationRequestDto paginationDto) {
        var pageable = PageRequest.of(paginationDto.getPageNumber(), paginationDto.getPageSize(), Sort.by(Sort.Direction.DESC, "Id"));
        var customerPage = customerRepository.findAll(pageable);
        var customerDtoPage  = customerPage.map(customer -> modelMapper.map(customer, CustomerResponseDto.class));
        return new PagedResponseDto<>(customerDtoPage);
    }

    @Cacheable(value = CUSTOMER_BY_ID_CACHE)
    public CustomerResponseDto getById(Long customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("error.customer.notFound"));

        return modelMapper.map(customer, CustomerResponseDto.class);
    }

    @Cacheable(value = CUSTOMER_PAGES_CACHE)
    public PagedResponseDto<CustomerResponseDto> filter(FilterInfoDto filterInfo) {
        var pageable = PageRequest.of(
                filterInfo.getFilterPage().getPage(),
                filterInfo.getFilterPage().getSize(),
                Sort.by(filterInfo.getFilterSort().getDirection(),
                        filterInfo.getFilterSort().getProps())
        );

        var filtersList = filterInfo.getFilters().stream()
                .map(filter -> modelMapper.map(filter, BaseFilter.class))
                .toList();

        var spec = new FilterSpecification<Customer>(filtersList);
        var customerPage = customerRepository.findAll(spec, pageable);
        var customerDtoPage = customerPage.map(customer -> modelMapper.map(customer, CustomerResponseDto.class));

        return new PagedResponseDto<>(customerDtoPage);
    }

    @CacheEvict(value = CUSTOMER_PAGES_CACHE, allEntries = true)
    public CustomerResponseDto create(AddCustomerRequestDto customerDto) {
        var customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerResponseDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = CUSTOMER_PAGES_CACHE, allEntries = true)
    }, put = {
            @CachePut(cacheNames = CUSTOMER_BY_ID_CACHE)
    })
    public CustomerResponseDto update(Long customerId, EditCustomerRequestDto customerDto) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("error.customer.notFound"));

        modelMapper.map(customerDto, customer);
        customerRepository.save(customer);

        return modelMapper.map(customer, CustomerResponseDto.class);
    }

    @Caching(evict = {
            @CacheEvict(value = CUSTOMER_PAGES_CACHE, allEntries = true),
            @CacheEvict(cacheNames = CUSTOMER_BY_ID_CACHE)
    })
    public void delete(Long customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("error.customer.notFound"));

        try {
            customerRepository.delete(customer);
        }
        catch (DataIntegrityViolationException ex) {
            throw new BusinessException("error.public.dependent.entity");
        }
    }
}
