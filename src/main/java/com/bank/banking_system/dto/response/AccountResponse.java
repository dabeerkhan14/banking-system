package com.bank.banking_system.dto.response;

import com.bank.banking_system.domain.enums.AccountStatus;
import com.bank.banking_system.domain.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AccountResponse
{
    private Long id;
    private String accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
    private LocalDateTime createdAt;
}
