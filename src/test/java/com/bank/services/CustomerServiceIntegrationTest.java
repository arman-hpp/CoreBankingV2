package com.bank.services;

import com.bank.dtos.customers.CustomerDto;
import com.bank.repos.customers.CustomerRepository;
import com.bank.services.customers.CustomerService;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerServiceIntegrationTest {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        // customerRepository.deleteAll();
    }

    @Test
    @Order(1)
    void addCustomer_shouldPersistToDatabase() {
        var dto = new CustomerDto();
        dto.setFirstName("Arman");
        dto.setLastName("Hasanpour");
        dto.setAddress("Iran, Mazandaran, Babol, Tohid 20");

        var saved = customerService.addOrEditCustomer(dto);

        assertNotNull(saved.getId());

        var fromDb = customerRepository.findById(saved.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("Arman Hasanpour", fromDb.get().getFullName());
    }
}
