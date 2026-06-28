package com.bank.banking_system.dto.response;

import com.bank.banking_system.domain.enums.TransactionStatus;
import com.bank.banking_system.domain.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse
{
    private Long id;
    private String referenceNumber;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private TransactionStatus status;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime createdAt;
}
