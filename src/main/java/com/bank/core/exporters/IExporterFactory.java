package com.bank.core.exporters;

public interface IExporterFactory {
    IExporter CreateExporter(ExportTypes exportType);
}
