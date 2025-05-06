package com.bank.loans.repos;

import com.bank.loans.models.Loan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.bank.core.repos.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
    @Query(value = "SELECT l FROM Loan l JOIN FETCH l.customerAccount a JOIN FETCH l.customer ORDER BY l.id DESC")
    List<Loan> findLoanWithDetails();

    @Query(value = "SELECT l FROM Loan l JOIN FETCH l.customerAccount a JOIN FETCH l.customer WHERE l.id = ?1 ORDER BY l.id DESC")
    Optional<Loan> findLoanByIdWithDetails(Long id);

    List<Loan> findByCustomerIdOrderByRequestDate(Long customerId);

    List<Loan> findByCustomerAccountIdOrderByRequestDate(Long accountId);

    Optional<Loan> findByLoanAccountId(Long accountId);
}
