package com.bank.customers.services;

import com.bank.customers.dtos.CustomerDto;
import com.bank.customers.repos.CustomerRepository;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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
