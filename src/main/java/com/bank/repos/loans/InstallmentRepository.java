package com.bank.repos.loans;

import com.bank.models.loans.Installment;
import com.bank.models.loans.LoanInterestStatistics;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.bank.repos.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InstallmentRepository extends BaseRepository<Installment, Long> {
    List<Installment> findByLoanIdOrderByInstallmentNo(Long LoanId);

    List<Installment> findByLoanIdAndPaidOrderByInstallmentNo(Long loanId, Boolean paid, Pageable pageable);

    List<Installment> findByLoanIdAndPaidFalseOrderByInstallmentNo(Long loanId);

    List<Installment> findByLoanIdAndPaidTrueOrderByInstallmentNo(Long loanId);

    default List<Installment> findTopCountByLoanIdAndPaidOrderByInstallmentNo(Integer count, Long loanId, Boolean paid) {
        return findByLoanIdAndPaidOrderByInstallmentNo(loanId, paid, PageRequest.of(0, count));
    }

    @Query(value = "SELECT new com.bank.models.loans.LoanInterestStatistics(i.currency, sum(i.interestAmount)) FROM Installment i WHERE i.paid = true AND i.paidDate > ?1 AND i.paidDate < ?2 GROUP BY i.currency")
    List<LoanInterestStatistics> sumTotalInterests(LocalDateTime fromDateTime, LocalDateTime toDateTime);
}

