package com.bank.banking_system.service;

import com.bank.banking_system.domain.entity.Account;
import com.bank.banking_system.domain.entity.Transaction;
import com.bank.banking_system.domain.enums.TransactionStatus;
import com.bank.banking_system.domain.enums.TransactionType;
import com.bank.banking_system.dto.request.TransferRequest;
import com.bank.banking_system.dto.response.TransactionResponse;
import com.bank.banking_system.exception.AccountInactiveException;
import com.bank.banking_system.exception.AccountNotFoundException;
import com.bank.banking_system.exception.InsufficientFundsException;
import com.bank.banking_system.exception.SameAccountTransferException;
import com.bank.banking_system.repository.AccountRepository;
import com.bank.banking_system.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService
{
    private final AccountRepository accountRepository;
    private final AuditTransactionService auditTransactionService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse transfer(TransferRequest request){
        Long sourceAccountId= request.getSourceAccountId();
        Long destinationAccountId= request.getDestinationAccountId();

        Account sourceAccount=accountRepository.findById(sourceAccountId)
                .orElseThrow(()-> new AccountNotFoundException("Source account not found with Id: "+sourceAccountId));
        Account destinationAccount=accountRepository.findById(destinationAccountId)
                .orElseThrow(()-> new AccountNotFoundException("Destination account not found with Id: "+destinationAccountId));

        if (sourceAccount.getAccountNumber().equals(destinationAccount.getAccountNumber())){
            throw new SameAccountTransferException("Source and Destination account must be different");
        }

        if(!sourceAccount.isActive()){
            throw new AccountInactiveException("Source account is not active: "+sourceAccount.getStatus());
        }

        if(!destinationAccount.isActive()){
            throw new AccountInactiveException("Destination account is not active: "+destinationAccount.getStatus());
        }

        Transaction transaction= Transaction.builder()
                .account(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(request.getAmount())
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .referenceNumber(generateReferenceNumber())
                .transactionType(TransactionType.TRANSFER)
                .build();

        try {
            sourceAccount.debit(request.getAmount());
            destinationAccount.credit(request.getAmount());
            transaction.markCompleted();
            transactionRepository.save(transaction);
        } catch (IllegalStateException e) {
            transaction.markFailed();
            auditTransactionService.saveTransaction(transaction);
            throw new InsufficientFundsException("Insufficient funds in source account");
        }

        return mapToResponse(transaction,sourceAccount,destinationAccount);

    }

    private String generateReferenceNumber(){
        return "TXN-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 16);
    }

    private TransactionResponse mapToResponse(Transaction transaction, Account sourceAccount, Account destinationAccount){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .referenceNumber(transaction.getReferenceNumber())
                .sourceAccountNumber(sourceAccount.getAccountNumber())
                .destinationAccountNumber(destinationAccount.getAccountNumber())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();

    }
}
