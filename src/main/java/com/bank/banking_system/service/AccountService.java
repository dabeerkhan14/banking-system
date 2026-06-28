package com.bank.banking_system.service;

import com.bank.banking_system.domain.entity.Account;
import com.bank.banking_system.domain.enums.AccountStatus;
import com.bank.banking_system.dto.request.CreateAccountRequest;
import com.bank.banking_system.dto.response.AccountResponse;
import com.bank.banking_system.exception.AccountNotFoundException;
import com.bank.banking_system.exception.DuplicateAccountException;
import com.bank.banking_system.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService
{
    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request){
        //step 1 - check duplicate account number
        if(accountRepository.existsByAccountNumber(request.getAccountNumber())){
            throw new DuplicateAccountException(
                    "Account number already exists: "+request.getAccountNumber()
            );
        }
        //step 2 - Build entity from request
        Account account= Account.builder()
                .accountNumber(request.getAccountNumber())
                .ownerName(request.getOwnerName())
                .balance(request.getInitialDeposit())
                .accountType(request.getAccountType())
                .status(AccountStatus.ACTIVE)
                .build();

        //step 3 - Save
        Account savedAccount=accountRepository.save(account);

        //step 4 - Map to response and return
        return mapToResponse(savedAccount);
    }

    private AccountResponse mapToResponse(Account account){
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .balance(account.getBalance())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public AccountResponse getAccount(Long id){
        Account account=accountRepository.findById(id)
                .orElseThrow(()-> new AccountNotFoundException("No account found with Id: "+id));
        return mapToResponse(account);
    }

}
