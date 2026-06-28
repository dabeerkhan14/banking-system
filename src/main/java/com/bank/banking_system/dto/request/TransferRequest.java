package com.bank.banking_system.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransferRequest
{
    @NotNull(message = "Source account Id is required")
    @Min(value = 1, message = "Source account Id should be positive")
    private Long sourceAccountId;

    @NotNull(message = "Destination account Id is required")
    @Min(value = 1, message = "Destination account Id should be positive")
    private Long DestinationAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater then zero")
    @Digits(integer = 15,fraction = 4,message = "Invalid amount format")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description cannot exceed 100 characters")
    private String description;
}
