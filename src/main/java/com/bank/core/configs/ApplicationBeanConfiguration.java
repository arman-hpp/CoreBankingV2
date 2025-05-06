package com.bank.core.configs;

import com.bank.core.exporters.ExporterFactory;
import com.bank.core.exporters.IExporterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bank.loans.services.AmortizationLoanCalculator;
import com.bank.loans.services.DefaultLoanValidator;
import com.bank.loans.services.interfaces.ILoanCalculator;
import com.bank.loans.services.interfaces.ILoanValidator;
import com.bank.transactions.services.RandomTraceNoGenerator;
import com.bank.transactions.services.interfaces.ITraceNoGenerator;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ITraceNoGenerator traceNoGenerator() {
        return new RandomTraceNoGenerator();
    }

    @Bean
    public ILoanCalculator loanCalculator() {
        return new AmortizationLoanCalculator();
    }

    @Bean
    public ILoanValidator loanValidator() {
        return new DefaultLoanValidator();
    }

    @Bean
    public IExporterFactory exporterFactory() {
        return new ExporterFactory();
    }
}
