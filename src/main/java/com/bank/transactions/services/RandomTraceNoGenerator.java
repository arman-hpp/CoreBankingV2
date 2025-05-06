package com.bank.transactions.services;

import com.bank.transactions.services.interfaces.ITraceNoGenerator;

import java.util.UUID;

public class RandomTraceNoGenerator implements ITraceNoGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
