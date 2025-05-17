package com.bank.transactions.services;

import com.bank.transactions.services.interfaces.ITraceNoGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomTraceNoGenerator implements ITraceNoGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
