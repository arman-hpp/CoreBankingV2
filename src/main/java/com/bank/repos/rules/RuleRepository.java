package com.bank.repos.rules;

import com.bank.models.rules.Rule;
import com.bank.repos.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends BaseRepository<Rule, Long> {

}
