package com.bank.transactions.services;

import com.bank.transactions.dtos.TransactionDto;
import com.bank.transactions.dtos.TransferDto;
import com.bank.transactions.enums.TransactionTypes;
import com.bank.core.events.NewTransactionEvent;
import com.bank.transactions.models.Transaction;
import com.bank.accounts.repos.AccountRepository;
import com.bank.core.exceptions.BusinessException;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.transactions.repos.TransactionRepository;
import com.bank.transactions.services.interfaces.ITraceNoGenerator;
import com.bank.core.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@SuppressWarnings("unused")
public class TransactionService {
    private final TransactionRepository _transactionRepository;
    private final AccountRepository _accountRepository;
    private final ITraceNoGenerator _traceNoGenerator;
    private final ModelMapper _modelMapper;
    private final ApplicationEventPublisher _publisher;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              ITraceNoGenerator traceNoGenerator,
                              ModelMapper modelMapper,
                              ApplicationEventPublisher publisher) {
        _transactionRepository = transactionRepository;
        _accountRepository = accountRepository;
        _traceNoGenerator = traceNoGenerator;
        _modelMapper = modelMapper;
        _publisher = publisher;
    }

    @Async
    public CompletableFuture<List<TransactionDto>> loadTransactions(LocalDateTime fromDate, LocalDateTime toDate) {
        var transactions = _transactionRepository.findByRegDateWithDetails(fromDate, toDate);
        return CompletableFuture.completedFuture(mapToTransactionDtoList(transactions));
    }

    public TransactionDto loadTransactionByTraceNo(String traceNo) {
        var transaction = _transactionRepository.findByTraceNo(traceNo).orElse(null);
        if(transaction == null)
            throw new BusinessException("error.transaction.notFound");

        return mapToTransactionDto(transaction);
    }

    public List<TransactionDto> loadLastAccountTransactions(Long accountId) {
        var transactions = _transactionRepository.findTop5ByAccountIdOrderByRegDateDesc(accountId);
        return mapToTransactionDtoList(transactions);
    }

    public List<TransactionDto> loadLastBranchTransactions() {
        var transactions = _transactionRepository.findTop5ByOrderByRegDateDesc();
        return mapToTransactionDtoList(transactions);
    }

    public List<TransactionDto> loadUserTransactions(Long userId) {
        var transactions = _transactionRepository.findUserTransactionsWithDetails(userId);
        return mapToTransactionDtoList(transactions);
    }

    @Transactional
    public void transfer(TransferDto transferDto) {
        var srcTransaction = new TransactionDto(transferDto.getAmount(), TransactionTypes.DEBIT,
                transferDto.getSrcDescription(), transferDto.getSrcAccountId(),
                transferDto.getUserId(), transferDto.getSrcTraceNo(), transferDto.getCurrency());

        var desTransaction = new TransactionDto(transferDto.getAmount(), TransactionTypes.CREDIT,
                transferDto.getDesDescription(), transferDto.getDesAccountId(),
                transferDto.getUserId(), transferDto.getDesTraceNo(), transferDto.getCurrency());

        doTransaction(srcTransaction);
        doTransaction(desTransaction);
    }

    @Transactional
    public void doTransaction(TransactionDto transactionDto) {
        var account = _accountRepository.findById(transactionDto.getAccountId()).orElse(null);
        if(account == null)
            throw new BusinessException("error.account.notFound");

        if(account.getCurrency() != transactionDto.getCurrency())
            throw new BusinessException("error.transaction.currency.mismatch");

        if(transactionDto.getTransactionType() == TransactionTypes.CREDIT) {
            account.setBalance(account.getBalance().add(transactionDto.getAmount()));
        } else if (transactionDto.getTransactionType() == TransactionTypes.DEBIT) {
            if(account.getBalance().compareTo(transactionDto.getAmount()) < 0)
                throw new BusinessException("error.transaction.balance.notEnough");

            account.setBalance(account.getBalance().subtract(transactionDto.getAmount()));
        } else
            throw new BusinessException("error.transaction.transactionType.invalid");

        var transaction = _modelMapper.map(transactionDto, Transaction.class);
        transaction.setRegDate(LocalDateTime.now());
        transaction.setAccount(account);

        if(StringUtils.isNullOrEmpty(transaction.getTraceNo())) {
            transaction.setTraceNo(_traceNoGenerator.generate());
        }

        _accountRepository.save(account);
        _transactionRepository.save(transaction);

        _publisher.publishEvent(new NewTransactionEvent(this, transaction));
    }

    private TransactionDto mapToTransactionDto(Transaction transaction) {
        var transactionDto = _modelMapper.map(transaction, TransactionDto.class);
        var account = transaction.getAccount();
        if(account != null) {
            transactionDto.setAccountId(account.getId());
            transactionDto.setAccountCurrency(account.getCurrency());
            transactionDto.setAccountBalance(account.getBalance());
            transactionDto.setCurrency(account.getCurrency());

            var customer = account.getCustomer();
            if(customer != null) {
                transactionDto.setAccountCustomerName(customer.getFullName());
            }
        }

        return transactionDto;
    }

    private List<TransactionDto> mapToTransactionDtoList(List<Transaction> transactionList) {
        var transactionDtoList = new ArrayList<TransactionDto>();
        for(var transaction : transactionList) {
            transactionDtoList.add(mapToTransactionDto(transaction));
        }

        return transactionDtoList;
    }

}
