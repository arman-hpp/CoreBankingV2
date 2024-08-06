package com.bank.services.accounts;

import com.bank.dtos.accounts.AccountDto;
import com.bank.enums.accounts.AccountTypes;
import com.bank.enums.accounts.Currencies;
import com.bank.exceptions.DomainException;
import com.bank.models.accounts.Account;
import com.bank.models.customers.Customer;
import com.bank.repos.accounts.AccountRepository;
import com.bank.repos.customers.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public List<AccountDto> loadAccounts() {
        var accounts = _accountRepository.findAllAccountsWithDetails();
        var accountDtoList = new ArrayList<AccountDto>();
        for(var account : accounts) {
            var accountDto = _modelMapper.map(account, AccountDto.class);
            var customer = account.getCustomer();
            accountDto.setCustomerId(customer.getId());
            accountDto.setCustomerName(customer.getFullName());
            accountDtoList.add(accountDto);
        }

        return accountDtoList;
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

    public List<AccountDto> loadCustomerAccounts(Long customerId) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new DomainException("error.customer.notFound");

        var accounts = _accountRepository.findByCustomerIdOrderByIdDesc(customerId);
        var accountDtoList = new ArrayList<AccountDto>();
        for(var account : accounts) {
            accountDtoList.add(_modelMapper.map(account, AccountDto.class));
        }

        return accountDtoList;
    }

    public AccountDto loadBankAccount(Currencies currency) {
        var account = _accountRepository.findByAccountTypeAndCurrency(AccountTypes.BankAccount, currency).orElse(null);
        if(account == null)
            throw new DomainException("error.account.notFound");

        return _modelMapper.map(account, AccountDto.class);
    }

    public List<AccountDto> loadBankAccounts() {
        var accounts = _accountRepository.findByAccountType(AccountTypes.BankAccount);
        var accountDtoList = new ArrayList<AccountDto>();
        for(var account : accounts) {
            accountDtoList.add(_modelMapper.map(account, AccountDto.class));
        }

        return accountDtoList;
    }

    public void addAccount(AccountDto accountDto) {
        if(accountDto.getCustomerId() == null) {
            throw new DomainException("error.account.notCustomerFound");
        }

        var account = _modelMapper.map(accountDto, Account.class);
        account.setBalance(BigDecimal.valueOf(0L));
        account.setAccountType(AccountTypes.CustomerAccount);
        account.setCustomer(new Customer(accountDto.getCustomerId()));

        _accountRepository.save(account);
    }
}