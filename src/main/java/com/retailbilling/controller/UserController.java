package com.retailbilling.controller;

import com.retailbilling.dto.RegisterRequest;
import com.retailbilling.dto.LoginRequest;
import com.retailbilling.dto.ApiResponse;
import com.retailbilling.dto.AuthResponse;
import com.retailbilling.entity.User;
import com.retailbilling.service.JwtService;
import com.retailbilling.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User register(
            @RequestBody RegisterRequest request) {

        return userService.createNewUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {

        System.out.println("--- LOGIN ROUTE INTERCEPTED ---");
        System.out.println(
                "Received Username: "
                + loginRequest.getUsername()
        );

        try {

            User user = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            String primaryRole = user.getRoles()
                    .stream()
                    .map(role -> role.getName())
                    .findFirst()
                    .orElse("ROLE_USER");

            AuthResponse.UserData userData =
                    new AuthResponse.UserData(
                            user.getUsername(),
                            user.getEmail(),
                            primaryRole
                    );

            // Generate REAL JWT
            String accessToken =
                    jwtService.generateToken(
                            user.getUsername(),
                            primaryRole
                    );

            AuthResponse authResponse =  new AuthResponse(
                            accessToken,
                            userData
                    );

            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(401)
                    .body(
                        new ApiResponse<Object>(
                            false,
                            e.getMessage(),
                            null
                        )
                    );
        }
    }
}