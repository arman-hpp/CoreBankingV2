package com.bank.users.repos;

import com.bank.users.models.User;
import org.springframework.stereotype.Repository;
import com.bank.core.repos.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findAllByOrderByIdDesc();
}