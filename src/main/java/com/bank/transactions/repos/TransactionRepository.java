package com.bank.transactions.repos;

import com.bank.transactions.models.Transaction;
import com.bank.core.repos.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    List<Transaction> findTop5ByAccountIdOrderByRegDateDesc(Long accountId);

    List<Transaction> findTop5ByOrderByRegDateDesc();

    @Query(value = "SELECT t FROM Transaction t JOIN FETCH t.account a LEFT JOIN FETCH a.customer WHERE t.userId = ?1 ORDER BY t.id DESC")
    List<Transaction> findUserTransactionsWithDetails(Long userId);

    @Query(value = "SELECT t FROM Transaction t JOIN FETCH t.account a LEFT JOIN FETCH a.customer WHERE t.regDate >= ?1 AND t.regDate <= ?2 ORDER BY t.id DESC")
    List<Transaction> findByRegDateWithDetails(LocalDateTime from, LocalDateTime to);

    Optional<Transaction> findByTraceNo(String traceNo);
}
