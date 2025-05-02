package com.bank.repos.accounts;

import com.bank.enums.accounts.Currencies;
import com.bank.models.accounts.Account;
import com.bank.repos.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l LEFT JOIN FETCH a.customer c ORDER BY a.id DESC")
    Page<Account> findAllWithDetails(Pageable pageable);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l LEFT JOIN FETCH a.customer c WHERE a.currency = ?1 ORDER BY a.id DESC")
    Page<Account> findByCurrencyWithDetails(Currencies currency, Pageable pageable);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l LEFT JOIN FETCH a.customer c WHERE sl.id = ?1 ORDER BY a.id DESC")
    Page<Account> findBySubLedgerIdWithDetails(Long subLedgerId, Pageable pageable);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l LEFT JOIN FETCH a.customer c WHERE gl.id = ?1 ORDER BY a.id DESC")
    Page<Account> findByGeneralLedgerIdWithDetails(Long generalLedgerId, Pageable pageable);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l LEFT JOIN FETCH a.customer c WHERE l.id = ?1 ORDER BY a.id DESC")
    Page<Account> findByLedgerIdWithDetails(Long ledgerId, Pageable pageable);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l LEFT JOIN FETCH a.customer c WHERE a.id = ?1")
    Optional<Account> findWithDetails(Long accountId);

    @Query(value = "SELECT a FROM Account a JOIN FETCH a.subLedger sl JOIN FETCH sl.generalLedger gl JOIN FETCH gl.ledger l JOIN FETCH a.customer c WHERE c.id = ?1 ORDER BY a.id DESC")
    Page<Account> findByCustomerIdOrderByIdDesc(Long customerId, Pageable pageable);
}
