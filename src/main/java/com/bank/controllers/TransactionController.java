package com.bank.controllers;

import com.bank.dtos.transactions.TransactionDto;
import com.bank.dtos.transactions.TransactionReportInputDto;
import com.bank.exceptions.DomainException;
import com.bank.exporters.ExportTypes;
import com.bank.services.transactions.TransactionService;
import com.bank.services.users.AuthenticationService;
import com.bank.exporters.IExporterFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService _transactionService;
    private final AuthenticationService _authenticationService;
    private final IExporterFactory _exporterFactory;

    public TransactionController(TransactionService transactionService,
                                 AuthenticationService authenticationService,
                                 IExporterFactory exporterFactory) {
        _transactionService = transactionService;
        _authenticationService = authenticationService;
        _exporterFactory = exporterFactory;
    }

    @GetMapping("/by/account")
    public List<TransactionDto> getLastTransactions(@RequestParam Long accountId) {
        return  _transactionService.loadLastAccountTransactions(accountId);
    }

    @GetMapping("/by/me")
    public List<TransactionDto> getCurrentUserTransactions() {
        var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
        if (currentUserId == null) {
            throw new DomainException("error.auth.credentials.invalid");
        }

        return  _transactionService.loadUserTransactions(currentUserId);
    }

    @GetMapping("/by/user")
    public List<TransactionDto> getUserTransactions(@RequestParam Long userId) {
        return  _transactionService.loadUserTransactions(userId);
    }

    @GetMapping("/by/branch")
    public List<TransactionDto> getLastBranchTransactions() {
        return  _transactionService.loadLastBranchTransactions();
    }

    @PostMapping("/")
    public void doTransaction(@RequestBody TransactionDto transactionDto){
        var currentUserId = _authenticationService.loadCurrentUserId().orElse(null);
        if (currentUserId == null) {
            throw new DomainException("error.auth.credentials.invalid");
        }
        transactionDto.setUserId(currentUserId);
        _transactionService.doTransaction(transactionDto);
    }

    @PostMapping("/export")
    public void export(@NonNull HttpServletResponse response,
                       @RequestBody TransactionReportInputDto transactionReportInputDto) {
        response.setContentType("application/octet-stream");

        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        var currentDateTime = LocalDateTime.now().format(dateFormatter);
        var exportType = ExportTypes.valueOf(transactionReportInputDto.getExportType());

        String headerKey = "Content-Disposition";
        String headerValue =
                "attachment; filename=transactions_" +
                        currentDateTime +
                        exportType.getFileExtension();
        response.setHeader(headerKey, headerValue);

        var future = _transactionService
                .loadTransactions(transactionReportInputDto.getFromDate(), transactionReportInputDto.getToDate());

        try {
            var transactionDtoList = future.get();

            var exporter = _exporterFactory.CreateExporter(exportType);
            exporter.export(response, TransactionDto.class, transactionDtoList);
        }
        catch (IOException | InterruptedException | ExecutionException ex) {
            throw new DomainException("error.public.unexpected");
        }
    }
}
