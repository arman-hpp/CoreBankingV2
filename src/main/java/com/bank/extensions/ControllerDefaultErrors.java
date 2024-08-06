package com.bank.extensions;

import lombok.Getter;

@Getter
public enum ControllerDefaultErrors {
    UnhandledError("error.public.unexpected"),
    InvalidInputParameters("error.public.inputs.invalid"),
    IllegalAccess("error.public.illegal.access");

    private final String displayMessage;

    ControllerDefaultErrors(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }
}
