package com.bank.accounts.services;

import com.bank.accounts.dtos.AccountResponseDto;
import com.bank.accounts.dtos.AddAccountRequestDto;
import com.bank.core.dtos.PagedResponseDto;
import com.bank.core.enums.Currencies;
import com.bank.core.exceptions.BusinessException;
import com.bank.accounts.models.Account;
import com.bank.customers.models.Customer;
import com.bank.ledgers.models.SubLedger;
import com.bank.accounts.repos.AccountRepository;
import com.bank.customers.repos.CustomerRepository;
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

    public AccountService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            ModelMapper modelMapper) {
        _accountRepository = accountRepository;
        _customerRepository = customerRepository;
        _modelMapper = modelMapper;
    }

    /**
     * Retrieves a paginated list of all accounts with all details.
     * @param page page the page number to retrieve (0-based index)
     * @param size size the number of accounts per page
     * @return a PagedResponseDto containing a list of AccountResponseDto objects and pagination metadata
     */
    public PagedResponseDto<AccountResponseDto> loadAccounts(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findAllWithDetails(pageable);
        var results = accounts.map(this::mapAccountToDto);
        return new PagedResponseDto<>(results);
    }

    /**
     * Retrieves a paginated list of accounts by specific currency with all details.
     * @param page page the page number to retrieve (0-based index)
     * @param size size the number of accounts per page
     * @return a PagedResponseDto containing a list of AccountResponseDto objects and pagination metadata
     */
    public PagedResponseDto<AccountResponseDto> loadAccounts(Currencies currency, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findByCurrencyWithDetails(currency, pageable);
        var results = accounts.map(this::mapAccountToDto);
        return new PagedResponseDto<>(results);
    }

    /**
     * Retrieves a paginated list of accounts filtered by sub-ledger ID and currency.
     * @param subLedgerId the ID of the sub-ledger to filter accounts by
     * @param page the page number to retrieve (0-based index)
     * @param size the number of accounts per page
     * @return a PagedResponseDto containing a list of AccountResponseDto objects matching the criteria
     */
    public PagedResponseDto<AccountResponseDto> loadAccountsBySubLedgerId(Long subLedgerId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findBySubLedgerIdWithDetails(subLedgerId, pageable);
        var results = accounts.map(this::mapAccountToDto);
        return new PagedResponseDto<>(results);
    }

    /**
     * Retrieves a paginated list of accounts filtered by general-ledger ID and currency.
     * @param generalLedgerId the ID of the general-ledger to filter accounts by
     * @param page the page number to retrieve (0-based index)
     * @param size the number of accounts per page
     * @return a PagedResponseDto containing a list of AccountResponseDto objects matching the criteria
     */
    public PagedResponseDto<AccountResponseDto> loadAccountsByGeneralLedgerId(Long generalLedgerId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findByGeneralLedgerIdWithDetails(generalLedgerId, pageable);
        var results = accounts.map(this::mapAccountToDto);
        return new PagedResponseDto<>(results);
    }

    /**
     * Retrieves a paginated list of accounts filtered by ledger ID and currency.
     * @param ledgerId the ID of the general-ledger to filter accounts by
     * @param page the page number to retrieve (0-based index)
     * @param size the number of accounts per page
     * @return a PagedResponseDto containing a list of AccountResponseDto objects matching the criteria
     */
    public PagedResponseDto<AccountResponseDto> loadAccountsByLedgerId(Long ledgerId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findByLedgerIdWithDetails(ledgerId, pageable);
        var results = accounts.map(this::mapAccountToDto);
        return new PagedResponseDto<>(results);
    }

    /**
     * Retrieves the details of a specific account by its ID.
     * @param accountId the unique identifier of the account to load
     * @return the AccountResponseDto containing account details
     */
    public AccountResponseDto loadAccount(Long accountId) {
        var account = _accountRepository.findWithDetails(accountId).orElse(null);
        if(account == null)
            throw new BusinessException("error.account.notFound");

        return mapAccountToDto(account);
    }

    /**
     * Retrieves a paginated list of specific customer accounts with all details.
     * @param customerId the unique identifier of the customer to load
     * @param page page the page number to retrieve (0-based index)
     * @param size size the number of accounts per page
     * @return a PagedResponseDto containing a list of AccountResponseDto objects and pagination metadata
     */
    public PagedResponseDto<AccountResponseDto> loadCustomerAccounts(Long customerId, int page, int size) {
        var customer = _customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new BusinessException("error.customer.notFound");

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "Id"));
        var accounts = _accountRepository.findByCustomerIdOrderByIdDesc(customerId, pageable);

        var results = accounts.map(this::mapAccountToDto);
        return new PagedResponseDto<>(results);
    }

    /**
     * Creates a new account with the provided account details.
     * @param accountDto the account data transfer object containing the information for the new account
     * @return the created AccountResponseDto with generated ID and additional saved information
     */
    public AccountResponseDto addAccount(AddAccountRequestDto accountDto) {
        if(accountDto.getCustomerId() == null) {
            throw new BusinessException("error.account.notCustomerFound");
        }

        var account = new Account();
        account.setCurrency(accountDto.getCurrency());
        account.setBalance(BigDecimal.valueOf(0L));
        account.setSubLedger(new SubLedger(accountDto.getSubLedgerId()));
        account.setCustomer(new Customer(accountDto.getCustomerId()));

        _accountRepository.save(account);

        return mapAccountToDto(account);
    }

    /**
     * Maps an Account entity to its corresponding AccountDto.
     * @param account the Account entity to be converted
     * @return the mapped AccountResponseDto containing the account's data
     */
    private AccountResponseDto mapAccountToDto(Account account) {
        var accountDto = _modelMapper.map(account, AccountResponseDto.class);

        var subLedger = account.getSubLedger();
        accountDto.setSubLedgerId(subLedger.getId());

        var generalLedger = subLedger.getGeneralLedger();
        accountDto.setGeneralLedgerId(generalLedger.getId());

        var ledger = generalLedger.getLedger();
        accountDto.setLedgerId(ledger.getId());

        accountDto.setAccountType(account.getDerivedAccountType());

        var customer = account.getCustomer();
        if(customer != null) {
            accountDto.setCustomerId(customer.getId());
            accountDto.setCustomerName(customer.getFullName());
        }

        return accountDto;
    }
}