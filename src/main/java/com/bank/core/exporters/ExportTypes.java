package com.bank.core.exporters;

import lombok.Getter;

@Getter
public enum ExportTypes {
    Excel(".xlsx"),
    CSV(".csv"),
    PDF(".pdf");

    private final String fileExtension;

    ExportTypes(String fileExtension) {
        this.fileExtension = fileExtension;
    }

}
