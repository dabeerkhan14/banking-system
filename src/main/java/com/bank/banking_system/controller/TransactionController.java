package com.bank.banking_system.controller;

import com.bank.banking_system.dto.request.TransferRequest;
import com.bank.banking_system.dto.response.TransactionResponse;
import com.bank.banking_system.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController
{
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> performTransaction(@Valid @RequestBody TransferRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.transfer(request));
    }
}
