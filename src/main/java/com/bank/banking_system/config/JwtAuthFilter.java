package com.bank.banking_system.config;

import com.bank.banking_system.service.JwtService;
import com.bank.banking_system.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1 — Read Authorization header
        final String authHeader = request.getHeader("Authorization");

        // Step 2 — If no header or doesn't start with Bearer, skip this filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3 — Extract JWT (everything after Bearer )
        final String jwt = authHeader.substring(7);

        // Step 4 — Extract username from token
        final String userEmail = jwtService.extractUsername(jwt);

        // Step 5 — If we have a username and no existing authentication
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6 — Load user from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Step 7 — Validate token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Step 8 — Create authentication object and set in SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 9 — Continue the filter chain
        filterChain.doFilter(request, response);
    }
}