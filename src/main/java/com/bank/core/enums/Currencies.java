package com.bank.core.enums;

import lombok.Getter;

@Getter
public enum Currencies {
    RIAL("R"),
    DOLLAR("$");

    private final String symbol;

    Currencies(String symbol) {
        this.symbol = symbol;
    }
}