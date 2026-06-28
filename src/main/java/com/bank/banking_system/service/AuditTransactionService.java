package com.bank.banking_system.service;


import com.bank.banking_system.domain.entity.Transaction;
import com.bank.banking_system.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditTransactionService
{

    private final TransactionRepository transactionRepository;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTransaction(Transaction transaction){
        transactionRepository.save(transaction);
    }
}
