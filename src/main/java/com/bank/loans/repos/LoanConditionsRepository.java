package com.bank.loans.repos;

import com.bank.core.enums.Currencies;
import com.bank.loans.models.LoanCondition;
import org.springframework.stereotype.Repository;
import com.bank.core.repos.BaseRepository;

import java.util.Optional;

@Repository
public interface LoanConditionsRepository extends BaseRepository<LoanCondition, Long> {
    Optional<LoanCondition> findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(Currencies currency);
}
