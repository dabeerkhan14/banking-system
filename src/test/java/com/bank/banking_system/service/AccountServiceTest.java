package com.bank.banking_system.service;
import com.bank.banking_system.domain.entity.Account;
import com.bank.banking_system.domain.enums.AccountStatus;
import com.bank.banking_system.domain.enums.AccountType;
import com.bank.banking_system.dto.request.CreateAccountRequest;
import com.bank.banking_system.dto.response.AccountResponse;
import com.bank.banking_system.exception.AccountNotFoundException;
import com.bank.banking_system.exception.DuplicateAccountException;
import com.bank.banking_system.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest
{
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountSuccessfully(){

        Account testingAccount=getDummyAccount();
        when(accountRepository.existsByAccountNumber("TX-12")).thenReturn(Boolean.FALSE);
        when(accountRepository.save(any(Account.class))).thenReturn(testingAccount);

        CreateAccountRequest request=CreateAccountRequest.builder()
                .accountNumber("TX-12")
                .ownerName("Dabeer Khan")
                .accountType(AccountType.CURRENT)
                .initialDeposit(new BigDecimal("30000"))
                .build();

        AccountResponse response=accountService.createAccount(request);

        assertThat(response.getOwnerName()).isEqualTo(testingAccount.getOwnerName());
        assertThat(response.getAccountNumber()).isEqualTo(testingAccount.getAccountNumber());
        assertThat(response.getBalance()).isEqualByComparingTo(testingAccount.getBalance());
        assertThat(response.getAccountType()).isEqualTo(testingAccount.getAccountType());
        assertThat(response.getStatus()).isEqualTo(testingAccount.getStatus());


    }

    @Test
    void shouldThrowExceptionWhenDuplicateAccountFound(){
        when(accountRepository.existsByAccountNumber("TX-12")).thenReturn(Boolean.TRUE);
        CreateAccountRequest request=CreateAccountRequest.builder()
                .accountNumber("TX-12")
                .ownerName("Dabeer Khan")
                .accountType(AccountType.CURRENT)
                .initialDeposit(new BigDecimal("30000"))
                .build();
        assertThrows(DuplicateAccountException.class, () -> {
            accountService.createAccount(request); // ← service should throw, not you
        });
    }

    @Test
    void shouldGetAccountById(){
        when(accountRepository.findById(1L)).thenReturn(Optional.of(getDummyAccount()));
        AccountResponse response=accountService.getAccount(1L);
        assertThat(response.getOwnerName()).isEqualTo(getDummyAccount().getOwnerName());
        assertThat(response.getAccountNumber()).isEqualTo(getDummyAccount().getAccountNumber());
        assertThat(response.getBalance()).isEqualByComparingTo(getDummyAccount().getBalance());
        assertThat(response.getAccountType()).isEqualTo(getDummyAccount().getAccountType());
        assertThat(response.getStatus()).isEqualTo(getDummyAccount().getStatus());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound(){
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class,()->{
            accountService.getAccount(1L);
        });
    }

    private Account getDummyAccount(){
        return  Account.builder().
                accountNumber("TX-12").
                ownerName("Dabeer Khan").
                balance(new BigDecimal("30000")).
                accountType(AccountType.CURRENT).
                status(AccountStatus.ACTIVE).
                createdAt(LocalDateTime.now()).
                updatedAt(LocalDateTime.now()).
                build();
    }

}
