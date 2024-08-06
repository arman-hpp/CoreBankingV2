package com.bank.services.transactions;

import com.bank.services.transactions.interfaces.ITraceNoGenerator;

import java.util.UUID;

public class RandomTraceNoGenerator implements ITraceNoGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
