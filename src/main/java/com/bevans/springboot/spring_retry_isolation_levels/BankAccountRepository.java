package com.bevans.springboot.spring_retry_isolation_levels;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BankAccountRepository extends Repository<BankAccount, Long> {
    Optional<BankAccount> findById(Long id);
}
