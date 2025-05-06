package com.bank.core.repos;

import com.bank.core.models.rules.Rule;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends BaseRepository<Rule, Long> {

}
