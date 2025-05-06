package com.bank.customers.repos;

import com.bank.customers.models.Customer;
import org.springframework.stereotype.Repository;
import com.bank.core.repos.BaseRepository;

import java.util.List;

@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long> {
    List<Customer> findAllByOrderByIdDesc();
}
