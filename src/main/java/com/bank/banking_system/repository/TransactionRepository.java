package com.bank.banking_system.repository;

import com.bank.banking_system.domain.entity.Account;
import com.bank.banking_system.domain.entity.Transaction;
import com.bank.banking_system.domain.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>
{
    List<Transaction> findByAccount(Account account);
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    List<Transaction> findByStatus(TransactionStatus status);
}
