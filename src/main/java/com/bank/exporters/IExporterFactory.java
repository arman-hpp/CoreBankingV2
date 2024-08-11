package com.bank.exporters;

public interface IExporterFactory {
    IExporter CreateExporter(ExportTypes exportType);
}
