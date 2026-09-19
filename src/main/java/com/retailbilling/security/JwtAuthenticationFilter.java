package com.retailbilling.security;

import com.retailbilling.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println(
                "JWT FILTER -> "
                + request.getRequestURI()
                + " -> "
                + authHeader
        );

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            if (jwtService.isTokenValid(token)) {

                System.out.println("JWT VALID");

                String username =
                        jwtService.extractUsername(token);

                String role =
                        jwtService.extractRole(token);

                System.out.println(
                        "JWT USER = " + username
                );

                System.out.println(
                        "JWT ROLE = " + role
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(role)
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            } else {

                System.out.println("JWT INVALID OR EXPIRED");
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR: " + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}