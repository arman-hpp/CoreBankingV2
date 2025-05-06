package com.bank.loans.services;

import com.bank.core.enums.Currencies;
import com.bank.loans.repos.LoanConditionsRepository;
import com.bank.loans.dtos.LoanConditionsDto;
import com.bank.core.exceptions.BusinessException;
import com.bank.loans.models.LoanCondition;
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
        if(currency == null)
            throw new BusinessException("error.loan.conditions.currency.invalid");

        var loanConditions = _loanConditionsRepository
                .findTop1ByCurrencyAndExpireDateIsNullOrderByStartDateDesc(currency).orElse(null);
        if(loanConditions == null)
            throw new BusinessException("error.loan.conditions.notFound");

        return _modelMapper.map(loanConditions, LoanConditionsDto.class);
    }

    @Transactional
    public void editLoanConditions(LoanConditionsDto loanConditionsDto) {
        var currentLoanConfigs = _loanConditionsRepository.findById(loanConditionsDto.getId()).orElse(null);
        if(currentLoanConfigs == null)
            throw new BusinessException("error.loan.conditions.notFound");

        currentLoanConfigs.setExpireDate(LocalDateTime.now());

        var newLoanConfigs = _modelMapper.map(loanConditionsDto, LoanCondition.class);
        newLoanConfigs.setStartDate(LocalDateTime.now());
        newLoanConfigs.setExpireDate(null);

        _loanConditionsRepository.save(newLoanConfigs);
        _loanConditionsRepository.save(currentLoanConfigs);
    }
}
