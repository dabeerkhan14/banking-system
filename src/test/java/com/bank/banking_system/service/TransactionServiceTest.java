package com.bank.banking_system.service;

import com.bank.banking_system.domain.entity.Account;
import com.bank.banking_system.domain.entity.Transaction;
import com.bank.banking_system.domain.enums.AccountStatus;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest
{
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuditTransactionService auditTransactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    //test 1
    public void shouldTransferSuccessfully(){
        when(accountRepository.findById(1L)).thenReturn(Optional.of(getDummySourceAccount()));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(getDummyDestinationAccount()));

        when(transactionRepository.save(any(Transaction.class))).thenReturn(getDummyTransaction());

        TransactionResponse response=transactionService.transfer(getDummyTransferRequest());

        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.getAmount()).isEqualByComparingTo("1000");



    }

    @Test
    //test 2
    public void shouldThrowExceptionWhenInsufficientFundsFound(){
        Account poorAccount = Account.builder()
                .id(1L)
                .accountNumber("TX-001")
                .balance(new BigDecimal("100"))  // transfer amount is 1000 — will fail
                .status(AccountStatus.ACTIVE)
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(poorAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(getDummyDestinationAccount()));


        assertThrows(InsufficientFundsException.class,()->{
            transactionService.transfer(getDummyTransferRequest());
        });
    }

    @Test
    //test 3
    public void shouldTrowExceptionWhenSameAccountTransferFound(){
        TransferRequest sameAccountRequest = TransferRequest.builder()
                .sourceAccountId(1L)
                .destinationAccountId(1L)  // same as source
                .amount(new BigDecimal("1000"))
                .description("Test")
                .build();


        assertThrows(SameAccountTransferException.class,()->{
            transactionService.transfer(sameAccountRequest);
        });
    }

    @Test
    //test 4
    public void shouldThrowExceptionWhenAccountNotFound(){
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class,()->{
            transactionService.transfer(getDummyTransferRequest());
        });
    }

    @Test
    //test 5
    public void shouldThrowExceptionWhenInactiveSourceAccountFound(){
        Account sourceAccount=Account.builder()
                .id(1L)
                .accountNumber("TX-001")
                .status(AccountStatus.SUSPENDED)
                .build();
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(getDummyDestinationAccount()));


        assertThrows(AccountInactiveException.class,()->{
            transactionService.transfer(getDummyTransferRequest());
        });
    }

    public Account getDummySourceAccount(){
        return Account.builder()
                .id(1L)
                .balance(new BigDecimal("50000"))
                .accountNumber("TX-001")
                .status(AccountStatus.ACTIVE)
                .build();
    }

    public Account getDummyDestinationAccount(){
        return Account.builder()
                .id(2L)
                .balance(new BigDecimal("50000"))
                .accountNumber("TX-002")
                .status(AccountStatus.ACTIVE)
                .build();
    }

    public Transaction getDummyTransaction(){
        return Transaction.builder()
                .amount(new BigDecimal("1000"))
                .status(TransactionStatus.COMPLETED)
                .referenceNumber(generateReferenceNumber())
                .build();
    }

    private TransferRequest getDummyTransferRequest() {
        return TransferRequest.builder()
                .sourceAccountId(1L)
                .destinationAccountId(2L)
                .amount(new BigDecimal("1000"))
                .description("Test transfer")
                .build();
    }

    private String generateReferenceNumber(){
        return "TXN-" + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 16);
    }
}
