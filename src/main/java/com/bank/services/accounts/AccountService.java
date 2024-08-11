package com.bank.services.accounts;

import com.bank.dtos.PagedResponseDto;
import com.bank.dtos.accounts.AccountDto;
import com.bank.enums.accounts.AccountTypes;
import com.bank.enums.accounts.Currencies;
import com.bank.exceptions.DomainException;
import com.bank.models.accounts.Account;
import com.bank.models.customers.Customer;
import com.bank.repos.accounts.AccountRepository;
import com.bank.repos.customers.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository _accountRepository;
    private final CustomerRepository _customerRepository;
    private final ModelMapper _modelMapper;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, ModelMapper modelMapper) {
        _accountRepository = accountRepository;
        _customerRepository = customerRepository;
        _modelMapper = modelMapper;
    }

    public PagedResponseDto<AccountDto> loadAccounts(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findAllAccountsWithDetails(pageable);

        var results = accounts.map(account -> {
            var accountDto = _modelMapper.map(account, AccountDto.class);
            var customer = account.getCustomer();
            accountDto.setCustomerId(customer.getId());
            accountDto.setCustomerName(customer.getFullName());

            return accountDto;
        });

        return new PagedResponseDto<>(results);
    }

    public AccountDto loadAccount(Long accountId) {
        var account = _accountRepository.findFromAllAccountWithDetails(accountId).orElse(null);
        if(account == null)
            throw new DomainException("error.account.notFound");

        var accountDto = _modelMapper.map(account, AccountDto.class);

        var customer = account.getCustomer();
        if(customer != null) {
            accountDto.setCustomerId(customer.getId());
            accountDto.setCustomerName(customer.getFullName());
        }

        return accountDto;
    }

    public AccountDto loadCustomerAccount(Long accountId) {
        var account = _accountRepository.findAccountWithDetails(accountId).orElse(null);
        if(account == null)
            throw new DomainException("error.account.notFound");

        var accountDto = _modelMapper.map(account, AccountDto.class);

        var customer = account.getCustomer();
        accountDto.setCustomerId(customer.getId());
        accountDto.setCustomerName(customer.getFullName());

        return accountDto;
    }

    public PagedResponseDto<AccountDto> loadCustomerAccounts(Long customerId, int page, int size) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new DomainException("error.customer.notFound");

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findByCustomerIdOrderByIdDesc(customerId, pageable);

        var results = accounts.map(account -> _modelMapper.map(account, AccountDto.class));
        return new PagedResponseDto<>(results);
    }

    public AccountDto loadBankAccount(Currencies currency) {
        var account = _accountRepository.findByAccountTypeAndCurrency(AccountTypes.BankAccount, currency).orElse(null);
        if(account == null)
            throw new DomainException("error.account.notFound");

        return _modelMapper.map(account, AccountDto.class);
    }

    public PagedResponseDto<AccountDto> loadBankAccounts(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findByAccountType(AccountTypes.BankAccount, pageable);
        var results = accounts.map(account -> _modelMapper.map(account, AccountDto.class));
        return new PagedResponseDto<>(results);
    }

    public AccountDto addAccount(AccountDto accountDto) {
        if(accountDto.getCustomerId() == null) {
            throw new DomainException("error.account.notCustomerFound");
        }

        var account = _modelMapper.map(accountDto, Account.class);
        account.setBalance(BigDecimal.valueOf(0L));
        account.setAccountType(AccountTypes.CustomerAccount);
        account.setCustomer(new Customer(accountDto.getCustomerId()));

        _accountRepository.save(account);

        accountDto.setId(account.getId());
        return accountDto;
    }
}