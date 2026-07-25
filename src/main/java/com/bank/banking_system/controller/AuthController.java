package com.bank.banking_system.controller;


import com.bank.banking_system.dto.request.LoginRequest;
import com.bank.banking_system.dto.request.RegisterRequest;
import com.bank.banking_system.dto.response.AuthResponse;
import com.bank.banking_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUser(@RequestBody @Valid RegisterRequest request){
       return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> getUser(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }
}
