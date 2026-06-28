package com.bank.banking_system.repository;

import com.bank.banking_system.domain.entity.Account;
import com.bank.banking_system.domain.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>
{
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findAllByStatus(AccountStatus status);
    boolean existsByAccountNumber(String accountNumber);
}
