package com.bank.services.loans;

import com.bank.enums.accounts.Currencies;
import com.bank.repos.loans.LoanConditionsRepository;
import com.bank.dtos.loans.LoanConditionsDto;
import com.bank.exceptions.DomainException;
import com.bank.models.loans.LoanCondition;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LoanConditionsService {
    private final LoanConditionsRepository _loanConditionsRepository;
    private final ModelMapper _modelMapper;

    public LoanConditionsService(LoanConditionsRepository loanConditionsRepository,
                                 ModelMapper modelMapper) {
        _loanConditionsRepository = loanConditionsRepository;
        _modelMapper = modelMapper;
    }

    public LoanConditionsDto loadLoanCondition(Currencies currency) {
        var loanConditions = _loanConditionsRepository
                .findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(currency).orElse(null);
        if(loanConditions == null)
            throw new DomainException("error.loan.conditions.notFound");

        return _modelMapper.map(loanConditions, LoanConditionsDto.class);
    }

    @Transactional
    public void editLoanConditions(LoanConditionsDto loanConditionsDto) {
        var currentLoanConfigs = _loanConditionsRepository.findById(loanConditionsDto.getId()).orElse(null);
        if(currentLoanConfigs == null)
            throw new DomainException("error.loan.conditions.notFound");

        currentLoanConfigs.setExpireDate(LocalDateTime.now());

        var newLoanConfigs = _modelMapper.map(loanConditionsDto, LoanCondition.class);
        newLoanConfigs.setStartDate(LocalDateTime.now());
        newLoanConfigs.setExpireDate(null);

        _loanConditionsRepository.save(newLoanConfigs);
        _loanConditionsRepository.save(currentLoanConfigs);
    }
}
