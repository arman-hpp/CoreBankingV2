package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.bank.core.repos",
        "com.bank.ledgers.repos",
        "com.bank.accounts.repos",
        "com.bank.customers.repos",
        "com.bank.transactions.repos",
        "com.bank.loans.repos",
        "com.bank.users.repos"
})
@EntityScan(basePackages = {
        "com.bank.core.models",
        "com.bank.ledgers.models",
        "com.bank.accounts.models",
        "com.bank.customers.models",
        "com.bank.transactions.models",
        "com.bank.loans.models",
        "com.bank.users.models"
})
@EnableCaching
public class CoreBankingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreBankingApplication.class, args);
    }
}
