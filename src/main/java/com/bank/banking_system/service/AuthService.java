package com.bank.banking_system.service;

import com.bank.banking_system.config.SecurityConfig;
import com.bank.banking_system.domain.entity.User;
import com.bank.banking_system.dto.request.LoginRequest;
import com.bank.banking_system.dto.request.RegisterRequest;
import com.bank.banking_system.dto.response.AuthResponse;
import com.bank.banking_system.exception.DuplicateUserException;
import com.bank.banking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request){
        String email=request.getEmail();
        Optional<User> existingUser=userRepository.findByEmail(email);
        if(existingUser.isPresent()){
            throw new DuplicateUserException("User already exist with email: "+email);
        }


        User user=User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser=userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request){

        var authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user= (User) authentication.getPrincipal();
        return mapToResponse(user);


    }

    private AuthResponse mapToResponse(UserDetails userDetails){
        return AuthResponse.builder()
                .token(jwtService.generateToken(userDetails))
                .build();
    }
}
